package com.superapp.dilucarmor.client;

import java.util.Locale;
import com.mojang.blaze3d.platform.InputConstants;
import com.superapp.dilucarmor.Abilities;
import com.superapp.dilucarmor.DilucArmor;
import com.superapp.dilucarmor.ModNetwork;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/** This entry point is never loaded on dedicated servers. */
@Mod(value = DilucArmor.MOD_ID, dist = Dist.CLIENT)
public final class DilucClient {
    private static final KeyMapping FIREBALL = new KeyMapping("key.dilucarmor.fireball",
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, "key.categories.dilucarmor");
    private static final KeyMapping PHOENIX = new KeyMapping("key.dilucarmor.phoenix",
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, "key.categories.dilucarmor");

    public DilucClient(IEventBus bus) {
        bus.addListener(DilucClient::keys);
        bus.addListener(DilucClient::renderers);
        bus.addListener(DilucClient::hud);
        NeoForge.EVENT_BUS.addListener(DilucClient::tick);
    }

    private static void keys(RegisterKeyMappingsEvent event) {
        event.register(FIREBALL);
        event.register(PHOENIX);
    }

    private static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(DilucArmor.FIREBALL.get(), FlameRenderer::new);
        event.registerEntityRenderer(DilucArmor.PHOENIX.get(), FlameRenderer::new);
    }

    private static void hud(RegisterGuiLayersEvent event) {
        event.registerAboveAll(DilucArmor.id("dawn_meter"), (graphics, delta) -> drawHud(graphics));
    }

    private static void tick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        // Consume clicks even with a screen open so a menu keypress cannot fire on closing.
        while (FIREBALL.consumeClick()) {
            if (mc.screen == null && !mc.player.isSpectator()) PacketDistributor.sendToServer(new ModNetwork.Cast(false));
        }
        while (PHOENIX.consumeClick()) {
            if (mc.screen == null && !mc.player.isSpectator()) PacketDistributor.sendToServer(new ModNetwork.Cast(true));
        }
        if (!mc.isPaused()) {
            CompoundTag tag = Abilities.data(mc.player);
            tag.putInt("fireCooldown", Math.max(0, tag.getInt("fireCooldown") - 1));
            tag.putInt("phoenixCooldown", Math.max(0, tag.getInt("phoenixCooldown") - 1));
        }
    }

    private static void drawHud(GuiGraphics gui) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || mc.player.isSpectator() || !DilucArmor.hasFullSet(mc.player)) return;
        CompoundTag tag = Abilities.data(mc.player);
        int charge = Math.clamp(tag.getInt("charge"), 0, Abilities.REQUIRED_CASTS);
        int fire = tag.getInt("fireCooldown");
        int phoenix = tag.getInt("phoenixCooldown");
        int x = 10, y = Math.max(8, gui.guiHeight() - 72);
        gui.fill(x - 4, y - 4, x + 202, y + 52, 0xB5181116);
        gui.drawString(mc.font, Component.translatable("hud.dilucarmor.title"), x, y, 0xFFFFC56A, true);
        String fireKey = FIREBALL.getTranslatedKeyMessage().getString();
        String phoenixKey = PHOENIX.getTranslatedKeyMessage().getString();
        Component fireLabel = fire > 0
                ? Component.translatable("hud.dilucarmor.fire_cooldown", fireKey, seconds(fire))
                : Component.translatable("hud.dilucarmor.fire_ready", fireKey);
        gui.drawString(mc.font, fireLabel, x, y + 13, 0xFFFFD5B4, true);
        Component burstLabel = charge == Abilities.REQUIRED_CASTS && phoenix == 0
                ? Component.translatable("hud.dilucarmor.phoenix_ready", phoenixKey)
                : phoenix > 0
                    ? Component.translatable("hud.dilucarmor.phoenix_cooldown", phoenixKey, seconds(phoenix))
                    : Component.translatable("hud.dilucarmor.charge", phoenixKey, charge, Abilities.REQUIRED_CASTS);
        gui.drawString(mc.font, burstLabel, x, y + 26,
                charge == Abilities.REQUIRED_CASTS ? 0xFFFFC13B : 0xFFFF9E70, true);
        for (int i = 0; i < Abilities.REQUIRED_CASTS; i++) {
            int left = x + i * 39;
            gui.fill(left, y + 40, left + 35, y + 46, 0xFF533039);
            if (i < charge) gui.fill(left + 1, y + 41, left + 34, y + 45, 0xFFFF8B26);
        }
    }

    private static String seconds(int ticks) { return String.format(Locale.ROOT, "%.1f", ticks / 20.0F); }
}

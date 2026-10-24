package com.superapp.dilucarmor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/** All damage, charge, equipment, and cooldown decisions are made on the server. */
public final class Abilities {
    public static final float FIREBALL_DAMAGE = 8.0F;
    public static final float PHOENIX_DAMAGE = FIREBALL_DAMAGE * 3.0F;
    public static final int REQUIRED_CASTS = 5;
    public static final int FIREBALL_COOLDOWN = 40;
    public static final int PHOENIX_COOLDOWN = 60;
    public static final String TAG = "DilucArmor";
    private Abilities() {}

    public static CompoundTag data(Player player) {
        CompoundTag root = player.getPersistentData();
        if (!root.contains(TAG, CompoundTag.TAG_COMPOUND)) root.put(TAG, new CompoundTag());
        return root.getCompound(TAG);
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        CompoundTag tag = data(player);
        tag.putInt("fireCooldown", Math.max(0, tag.getInt("fireCooldown") - 1));
        tag.putInt("phoenixCooldown", Math.max(0, tag.getInt("phoenixCooldown") - 1));
        if (player.isAlive() && !player.isSpectator() && DilucArmor.hasFullSet(player)) {
            // Short duration avoids removing Strength supplied by potions/other mods on unequip.
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 10, 0, true, false, true));
        }
        if (player.tickCount % 5 == 0) sync(player);
    }

    public static void onClone(PlayerEvent.Clone event) {
        // No stored burst on respawn, including when keepInventory is enabled.
        data(event.getEntity()).putInt("charge", 0);
        data(event.getEntity()).putInt("fireCooldown", 0);
        data(event.getEntity()).putInt("phoenixCooldown", 0);
    }

    public static void cast(ServerPlayer player, boolean ultimate) {
        if (!player.isAlive() || player.isSpectator()) return;
        if (!DilucArmor.hasFullSet(player)) {
            player.displayClientMessage(Component.translatable("message.dilucarmor.full_set"), true);
            return;
        }
        CompoundTag tag = data(player);
        String cooldown = ultimate ? "phoenixCooldown" : "fireCooldown";
        if (tag.getInt(cooldown) > 0) return;
        int charge = Math.clamp(tag.getInt("charge"), 0, REQUIRED_CASTS);
        if (ultimate && charge < REQUIRED_CASTS) {
            player.displayClientMessage(Component.translatable("message.dilucarmor.not_ready", charge, REQUIRED_CASTS), true);
            return;
        }

        FlameProjectile projectile = new FlameProjectile(
                ultimate ? DilucArmor.PHOENIX.get() : DilucArmor.FIREBALL.get(), player.level());
        projectile.setOwner(player);
        Vec3 direction = player.getLookAngle().normalize();
        // Start inside the eye position; ray collision prevents shooting through nearby walls.
        projectile.setPos(player.getEyePosition());
        projectile.shoot(direction.x, direction.y, direction.z, ultimate ? 1.15F : 1.6F, 0.0F);
        if (!player.level().addFreshEntity(projectile)) return;

        tag.putInt(cooldown, ultimate ? PHOENIX_COOLDOWN : FIREBALL_COOLDOWN);
        tag.putInt("charge", ultimate ? 0 : Math.min(REQUIRED_CASTS, charge + 1));
        player.level().playSound(null, player.blockPosition(),
                ultimate ? SoundEvents.ENDER_DRAGON_SHOOT : SoundEvents.BLAZE_SHOOT,
                SoundSource.PLAYERS, ultimate ? 1.2F : 0.7F, ultimate ? 0.85F : 1.15F);
        if (!ultimate && charge == REQUIRED_CASTS - 1) {
            player.displayClientMessage(Component.translatable("message.dilucarmor.ready"), true);
            player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP,
                    SoundSource.PLAYERS, 0.55F, 1.6F);
        }
        sync(player);
    }

    public static void sync(ServerPlayer player) {
        CompoundTag tag = data(player);
        PacketDistributor.sendToPlayer(player, new ModNetwork.State(
                Math.clamp(tag.getInt("charge"), 0, REQUIRED_CASTS),
                tag.getInt("fireCooldown"), tag.getInt("phoenixCooldown")));
    }
}

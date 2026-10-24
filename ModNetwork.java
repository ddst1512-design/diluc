package com.superapp.dilucarmor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetwork {
    private ModNetwork() {}

    public record Cast(boolean ultimate) implements CustomPacketPayload {
        public static final Type<Cast> TYPE = new Type<>(DilucArmor.id("cast"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Cast> CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Cast::ultimate, Cast::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public record State(int charge, int fireCooldown, int phoenixCooldown) implements CustomPacketPayload {
        public static final Type<State> TYPE = new Type<>(DilucArmor.id("state"));
        public static final StreamCodec<RegistryFriendlyByteBuf, State> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, State::charge,
                ByteBufCodecs.VAR_INT, State::fireCooldown,
                ByteBufCodecs.VAR_INT, State::phoenixCooldown, State::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(Cast.TYPE, Cast.CODEC, (payload, context) ->
                context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player) Abilities.cast(player, payload.ultimate());
                }));
        // No Minecraft client class references here, so dedicated servers can load this class safely.
        registrar.playToClient(State.TYPE, State.CODEC, (payload, context) ->
                context.enqueueWork(() -> {
                    CompoundTag tag = Abilities.data(context.player());
                    tag.putInt("charge", payload.charge());
                    tag.putInt("fireCooldown", payload.fireCooldown());
                    tag.putInt("phoenixCooldown", payload.phoenixCooldown());
                }));
    }
}

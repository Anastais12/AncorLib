package com.anchor_studio.anchorlib.system.ability.network;

import com.anchor_studio.anchorlib.AnchorLib;
import com.anchor_studio.anchorlib.system.ability.data.HotbarData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public record SyncHotbarDataPacket(String hotbarId, CompoundTag data) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(AnchorLib.MOD_ID, "sync_hotbar");
    public static final Type<SyncHotbarDataPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SyncHotbarDataPacket> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeUtf(packet.hotbarId());
                buf.writeNbt(packet.data());
            },
            buf -> new SyncHotbarDataPacket(buf.readUtf(), buf.readNbt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void send(ServerPlayer player, String hotbarId, HotbarData data) {
        ServerPlayNetworking.send(player, new SyncHotbarDataPacket(hotbarId, data.toNbt()));
    }

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
            context.client().execute(() -> {
                HotbarData hotbarData = HotbarData.fromNbt(payload.data());
                AnchorLib.LOGGER.debug("Received hotbar sync for {}", payload.hotbarId());
            });
        });
    }
}
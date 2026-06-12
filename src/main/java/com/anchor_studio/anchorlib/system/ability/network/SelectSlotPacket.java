package com.anchor_studio.anchorlib.system.ability.network;

import com.anchor_studio.anchorlib.AnchorLib;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * Client -> Server: Player selected a different hotbar slot
 */
public record SelectSlotPacket(int slotIndex, String hotbarId) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(AnchorLib.MOD_ID, "select_slot");
    public static final Type<SelectSlotPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SelectSlotPacket> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeVarInt(packet.slotIndex);
                buf.writeUtf(packet.hotbarId);
            },
            buf -> new SelectSlotPacket(buf.readVarInt(), buf.readUtf())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void send(int slotIndex, String hotbarId) {
        ClientPlayNetworking.send(new SelectSlotPacket(slotIndex, hotbarId));
    }

    public static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            context.server().execute(() -> {
                // Server-side slot change logic
                // Update server-side hotbar state, trigger ability selection events
                AnchorLib.LOGGER.debug("Player {} selected slot {} on hotbar {}",
                        player.getName().getString(), payload.slotIndex(), payload.hotbarId());
            });
        });
    }
}
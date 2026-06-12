package com.anchor_studio.anchorlib.system.ability.network;

import com.anchor_studio.anchorlib.AnchorLib;
import com.anchor_studio.anchorlib.system.ability.Ability;
import com.anchor_studio.anchorlib.system.ability.AbilityContext;
import com.anchor_studio.anchorlib.system.ability.AbilityRegistry;
import com.anchor_studio.anchorlib.system.ability.CooldownManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public record UseAbilityPacket(Identifier abilityId, int slotIndex) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(AnchorLib.MOD_ID, "use_ability");
    public static final Type<UseAbilityPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, UseAbilityPacket> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeIdentifier(packet.abilityId);
                buf.writeVarInt(packet.slotIndex);
            },
            buf -> new UseAbilityPacket(buf.readIdentifier(), buf.readVarInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void send(Identifier abilityId, int slotIndex) {
        ClientPlayNetworking.send(new UseAbilityPacket(abilityId, slotIndex));
    }

    public static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            context.server().execute(() -> {
                Ability ability = AbilityRegistry.get(payload.abilityId());
                if (ability == null) {
                    AnchorLib.LOGGER.warn("Unknown ability {} used by player {}", payload.abilityId(), player.getName().getString());
                    return;
                }

                AbilityContext abilityContext = AbilityContext.of(player, payload.slotIndex());

                if (!ability.canUse(abilityContext)) {
                    return;
                }

                ability.onUse(abilityContext);

                int cooldown = ability.getCooldownTicks();
                if (cooldown > 0) {
                    CooldownManager.setCooldown(player, payload.abilityId(), cooldown);
                }

                AnchorLib.LOGGER.debug("Player {} used ability {}", player.getName().getString(), payload.abilityId());
            });
        });
    }
}

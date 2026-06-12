package com.anchor_studio.anchorlib.system.ability.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * Central networking registration for AnchorLib
 */
public class AnchorLibNetworking {

    public static void registerCommon() {
        // Register payload types (common / both sides)
        PayloadTypeRegistry.playC2S().register(SelectSlotPacket.TYPE, SelectSlotPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(UseAbilityPacket.TYPE, UseAbilityPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncHotbarDataPacket.TYPE, SyncHotbarDataPacket.CODEC);

        // Register server receivers
        SelectSlotPacket.registerServerReceiver();
        UseAbilityPacket.registerServerReceiver();
    }

    public static void registerClient() {
        // Register client receivers
        SyncHotbarDataPacket.registerClientReceiver();
    }
}
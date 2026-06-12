package com.anchor_studio.anchorlib.client;

import com.anchor_studio.anchorlib.AnchorLib;
import com.anchor_studio.anchorlib.system.ability.input.HotbarInputHandler;
import com.anchor_studio.anchorlib.system.ability.network.AnchorLibNetworking;
import com.anchor_studio.anchorlib.system.ability.render.HotbarRenderer;
import net.fabricmc.api.ClientModInitializer;

public class AnchorLibClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AnchorLib.LOGGER.info("Initializing AnchorLib Client");

        AnchorLibNetworking.registerClient();

        HotbarInputHandler.init();
        HotbarRenderer.init();

        HotbarInputHandler.setActiveHotbar("anchorlib:ability_hotbar");

        AnchorLib.LOGGER.info("AnchorLib Client initialized");
    }
}
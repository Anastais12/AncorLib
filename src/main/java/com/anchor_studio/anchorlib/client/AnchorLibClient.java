package com.anchor_studio.anchorlib.client;

import com.anchor_studio.anchorlib.AnchorLib;
import net.fabricmc.api.ClientModInitializer;

public class AnchorLibClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AnchorLib.LOGGER.info("Initializing AnchorLib Client");
//-----------------------------------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------------------------------//
        AnchorLib.LOGGER.info("AnchorLib Client initialized");
    }
}
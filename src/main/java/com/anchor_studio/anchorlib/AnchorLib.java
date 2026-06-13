package com.anchor_studio.anchorlib;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnchorLib implements ModInitializer {

	public static final String MOD_ID = "anchorlib";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing AnchorLib");
//-----------------------------------------------------------------------------------------------------------------------//

//-----------------------------------------------------------------------------------------------------------------------//
		LOGGER.info("AnchorLib initialized successfully");
	}
}
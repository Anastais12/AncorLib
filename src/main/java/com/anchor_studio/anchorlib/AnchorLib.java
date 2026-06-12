package com.anchor_studio.anchorlib;

import com.anchor_studio.anchorlib.system.ability.AbilityRegistry;
import com.anchor_studio.anchorlib.system.ability.hotbar.HotbarRegistry;
import com.anchor_studio.anchorlib.system.ability.network.AnchorLibNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnchorLib implements ModInitializer {

	public static final String MOD_ID = "anchorlib";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing AnchorLib");

		// Register networking
		AnchorLibNetworking.registerCommon();

		// Server lifecycle
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			// Clear registries on server start (fresh state)
			AbilityRegistry.clear();
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			// Clean up player data on disconnect
			com.anchor_studio.anchorlib.system.ability.hotbar.HotbarRegistry.clearPlayer(handler.getPlayer());
			com.anchor_studio.anchorlib.system.ability.CooldownManager.clearCooldowns(handler.getPlayer());
		});


		//------------------------------------------------------------------------------------------------------------------///

		AbilityRegistry.register(new FireBallAbility());

		// Register hotbar factory - creates a hotbar instance per player
		HotbarRegistry.registerFactory("anchorlib:ability_hotbar", player -> new MyAbilityHotbar());

		// Give players the hotbar when they join (server-side)
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				if (!HotbarRegistry.hasHotbar(player, "mymod:ability_hotbar")) {
					HotbarRegistry.getOrCreate(player, "mymod:ability_hotbar");
				}
			}
		});

		LOGGER.info("AnchorLib initialized successfully");
	}
}
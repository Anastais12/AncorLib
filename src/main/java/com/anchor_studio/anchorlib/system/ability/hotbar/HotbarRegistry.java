package com.anchor_studio.anchorlib.system.ability.hotbar;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Per-player hotbar registry. Each player can have multiple hotbar instances.
 */
public class HotbarRegistry {

    private static final Map<UUID, Map<String, CustomHotbar>> PLAYER_HOTBARS = new HashMap<>();
    private static final Map<String, Function<Player, CustomHotbar>> HOTBAR_FACTORIES = new HashMap<>();

    /**
     * Register a hotbar type factory. Called by mods during initialization.
     */
    public static void registerFactory(String hotbarId, Function<Player, CustomHotbar> factory) {
        HOTBAR_FACTORIES.put(hotbarId, factory);
    }

    /**
     * Get or create a hotbar for a player
     */
    @Nullable
    public static CustomHotbar getOrCreate(Player player, String hotbarId) {
        Map<String, CustomHotbar> playerHotbars = PLAYER_HOTBARS.computeIfAbsent(player.getUUID(), k -> new HashMap<>());

        CustomHotbar hotbar = playerHotbars.get(hotbarId);
        if (hotbar == null) {
            Function<Player, CustomHotbar> factory = HOTBAR_FACTORIES.get(hotbarId);
            if (factory != null) {
                hotbar = factory.apply(player);
                playerHotbars.put(hotbarId, hotbar);
            }
        }
        return hotbar;
    }

    @Nullable
    public static CustomHotbar get(Player player, String hotbarId) {
        Map<String, CustomHotbar> playerHotbars = PLAYER_HOTBARS.get(player.getUUID());
        return playerHotbars != null ? playerHotbars.get(hotbarId) : null;
    }

    public static void set(Player player, String hotbarId, CustomHotbar hotbar) {
        PLAYER_HOTBARS.computeIfAbsent(player.getUUID(), k -> new HashMap<>()).put(hotbarId, hotbar);
    }

    public static void remove(Player player, String hotbarId) {
        Map<String, CustomHotbar> playerHotbars = PLAYER_HOTBARS.get(player.getUUID());
        if (playerHotbars != null) {
            playerHotbars.remove(hotbarId);
        }
    }

    public static void clearPlayer(Player player) {
        PLAYER_HOTBARS.remove(player.getUUID());
    }

    public static boolean hasHotbar(Player player, String hotbarId) {
        Map<String, CustomHotbar> playerHotbars = PLAYER_HOTBARS.get(player.getUUID());
        return playerHotbars != null && playerHotbars.containsKey(hotbarId);
    }
}
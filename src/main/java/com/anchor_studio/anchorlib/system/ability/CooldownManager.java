package com.anchor_studio.anchorlib.system.ability;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Per-player cooldown tracking for abilities.
 */
public class CooldownManager {

    private static final Map<UUID, Map<Identifier, Long>> COOLDOWNS = new HashMap<>();

    public static void setCooldown(Player player, Identifier abilityId, int ticks) {
        COOLDOWNS.computeIfAbsent(player.getUUID(), k -> new HashMap<>())
                .put(abilityId, player.level().getGameTime() + ticks);
    }

    public static boolean isOnCooldown(Player player, Identifier abilityId) {
        Map<Identifier, Long> playerCooldowns = COOLDOWNS.get(player.getUUID());
        if (playerCooldowns == null) return false;

        Long endTime = playerCooldowns.get(abilityId);
        if (endTime == null) return false;

        if (player.level().getGameTime() >= endTime) {
            playerCooldowns.remove(abilityId);
            return false;
        }
        return true;
    }

    public static int getRemainingTicks(Player player, Identifier abilityId) {
        Map<Identifier, Long> playerCooldowns = COOLDOWNS.get(player.getUUID());
        if (playerCooldowns == null) return 0;

        Long endTime = playerCooldowns.get(abilityId);
        if (endTime == null) return 0;

        long remaining = endTime - player.level().getGameTime();
        return remaining > 0 ? (int) remaining : 0;
    }

    public static void clearCooldowns(Player player) {
        COOLDOWNS.remove(player.getUUID());
    }
}
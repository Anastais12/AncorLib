package com.anchor_studio.anchorlib.system.ability;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * Context passed to ability methods containing player, world, and targeting info.
 */
public record AbilityContext(Player player, Level level, BlockHitResult blockHit, EntityHitResult entityHit, int slotIndex) {

    public AbilityContext(Player player, Level level, @Nullable BlockHitResult blockHit,
                          @Nullable EntityHitResult entityHit, int slotIndex) {
        this.player = player;
        this.level = level;
        this.blockHit = blockHit;
        this.entityHit = entityHit;
        this.slotIndex = slotIndex;
    }

    public boolean isClientSide() {
        return level.isClientSide();
    }

    @Override
    @Nullable
    public BlockHitResult blockHit() {
        return blockHit;
    }

    @Override
    @Nullable
    public EntityHitResult entityHit() {
        return entityHit;
    }

    @Nullable
    public BlockPos getTargetedBlockPos() {
        return blockHit != null ? blockHit.getBlockPos() : null;
    }

    public static AbilityContext of(Player player, int slotIndex) {
        return new AbilityContext(player, player.level(), null, null, slotIndex);
    }
}
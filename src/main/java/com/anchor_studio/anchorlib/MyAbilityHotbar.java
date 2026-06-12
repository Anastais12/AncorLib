package com.anchor_studio.anchorlib;

import com.anchor_studio.anchorlib.system.ability.AbilityContext;
import com.anchor_studio.anchorlib.system.ability.hotbar.CustomHotbar;
import com.anchor_studio.anchorlib.system.ability.hotbar.HotbarSlot;
import com.anchor_studio.anchorlib.system.ability.hotbar.SlotPosition;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MyAbilityHotbar extends CustomHotbar {

    public MyAbilityHotbar() {
        super(
                Identifier.fromNamespaceAndPath("anchorlib", "textures/gui/ability_hotbar.png"),
                new SlotPosition(3, 3, 16, 16),
                182, 22,
                List.of(
                        SlotPosition.of(3, 3),   // Slot 0
                        SlotPosition.of(3, 23),   // Slot 1
                        SlotPosition.of(3, 43),   // Slot 2
                        SlotPosition.of(3, 63),  // Slot 3
                        SlotPosition.of(3, 83)   // Slot 4
                )
        );

        // Assign abilities to slots
        setAbility(0, new FireBallAbility());

        // Slot 3 has a vanilla item
        setItem(3, new ItemStack(Items.DIAMOND_SWORD));

        // Slot 4 has custom data (anything you want)
        setCustomData(4, "special_buff_state");
    }

    /**
     * Called when ANY slot is used. Override for special behavior.
     * Slot indices are 0-based, so slot #5 is index 4.
     */
    @Override
    public void onSlotUse(int slotIndex, AbilityContext context) {
        // Special behavior for slot #5 (index 4)
        if (slotIndex == 4) {
            context.player().addDeltaMovement(Vec3.Y_AXIS);
            // Could trigger a custom buff, transformation, etc.
        }

        // Always call super for default ability behavior (cooldowns, etc.)
        super.onSlotUse(slotIndex, context);
    }
}
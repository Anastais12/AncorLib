package com.anchor_studio.anchorlib.system.ability.input;

import com.anchor_studio.anchorlib.system.ability.hotbar.CustomHotbar;
import com.anchor_studio.anchorlib.system.ability.hotbar.HotbarRegistry;
import com.anchor_studio.anchorlib.system.ability.network.SelectSlotPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.KeyMapping.Category;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class HotbarInputHandler {

    private static KeyMapping[] HOTBAR_KEYS = new KeyMapping[9];
    private static KeyMapping USE_ABILITY_KEY;
    private static KeyMapping NEXT_SLOT_KEY;
    private static KeyMapping PREV_SLOT_KEY;

    private static String activeHotbarId = "anchorlib:default";
    private static boolean enabled = true;

    public static void init() {
        Category category = Category.register(
                net.minecraft.resources.Identifier.fromNamespaceAndPath("anchorlib", "abilities")
        );

        for (int i = 0; i < 9; i++) {
            HOTBAR_KEYS[i] = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                    "key.anchorlib.hotbar." + (i + 1),
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_1 + i,
                    category
            ));
        }

        USE_ABILITY_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.anchorlib.use_ability",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                category
        ));

        NEXT_SLOT_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.anchorlib.next_slot",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_BRACKET,
                category
        ));

        PREV_SLOT_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.anchorlib.prev_slot",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_BRACKET,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!enabled || client.player == null) return;

            for (int i = 0; i < HOTBAR_KEYS.length; i++) {
                while (HOTBAR_KEYS[i].consumeClick()) {
                    selectSlot(i);
                }
            }

            while (USE_ABILITY_KEY.consumeClick()) {
                useSelectedSlot();
            }
            while (NEXT_SLOT_KEY.consumeClick()) {
                nextSlot();
            }
            while (PREV_SLOT_KEY.consumeClick()) {
                prevSlot();
            }

            CustomHotbar hotbar = getActiveHotbar(client.player);
            if (hotbar != null) {
                hotbar.tick();
            }
        });
    }

    private static CustomHotbar getActiveHotbar(LocalPlayer player) {
        return HotbarRegistry.get(player, activeHotbarId);
    }

    private static void selectSlot(int slot) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        CustomHotbar hotbar = getActiveHotbar(mc.player);
        if (hotbar != null && slot < hotbar.getSlotCount()) {
            hotbar.setSelectedSlot(slot);
            SelectSlotPacket.send(slot, activeHotbarId);
        }
    }

    private static void useSelectedSlot() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        CustomHotbar hotbar = getActiveHotbar(mc.player);
        if (hotbar != null) {
            hotbar.useSelectedSlot();
        }
    }

    private static void nextSlot() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        CustomHotbar hotbar = getActiveHotbar(mc.player);
        if (hotbar != null) {
            hotbar.selectNextSlot();
            SelectSlotPacket.send(hotbar.getSelectedSlot(), activeHotbarId);
        }
    }

    private static void prevSlot() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        CustomHotbar hotbar = getActiveHotbar(mc.player);
        if (hotbar != null) {
            hotbar.selectPreviousSlot();
            SelectSlotPacket.send(hotbar.getSelectedSlot(), activeHotbarId);
        }
    }

    public static void setActiveHotbar(String hotbarId) {
        activeHotbarId = hotbarId;
    }

    public static String getActiveHotbarId() {
        return activeHotbarId;
    }

    public static void setEnabled(boolean enabled) {
        HotbarInputHandler.enabled = enabled;
    }
}

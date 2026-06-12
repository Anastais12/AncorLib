package com.anchor_studio.anchorlib.system.ability.render;

import com.anchor_studio.anchorlib.system.ability.hotbar.CustomHotbar;
import com.anchor_studio.anchorlib.system.ability.hotbar.HotbarRegistry;
import com.anchor_studio.anchorlib.system.ability.hotbar.HotbarSlot;
import com.anchor_studio.anchorlib.system.ability.hotbar.SlotPosition;
import com.anchor_studio.anchorlib.system.ability.input.HotbarInputHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static com.anchor_studio.anchorlib.AnchorLib.MOD_ID;

public class HotbarRenderer {

    private static final Identifier DEFAULT_SLOT_TEXTURE =
            Identifier.fromNamespaceAndPath("anchorlib", "textures/gui/slot.png");
    private static final Identifier SELECTED_SLOT_TEXTURE =
            Identifier.fromNamespaceAndPath("anchorlib", "textures/gui/slot_selected.png");

    private static final Minecraft MC = Minecraft.getInstance();

    public static void init() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.HOTBAR,
                Identifier.fromNamespaceAndPath(MOD_ID, "ability_bar_hud"),
                HotbarRenderer::render
        );
    }

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Player player = MC.player;
        if (player == null) return;

        String activeId = HotbarInputHandler.getActiveHotbarId();
        CustomHotbar hotbar = HotbarRegistry.get(player, activeId);
        if (hotbar == null || !hotbar.isVisible()) return;

        renderBackground(graphics, hotbar);

        for (int i = 0; i < hotbar.getSlotCount(); i++) {
            renderSlot(graphics, hotbar, i);
        }

        renderSelection(graphics, hotbar);
    }

    private static void renderBackground(GuiGraphics graphics, CustomHotbar hotbar) {
        SlotPosition bgPos = hotbar.getBackgroundPosition();
        //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(
                hotbar.getBackgroundTexture(),
                bgPos.x(), bgPos.y(),
                0, 0,
                hotbar.getWidth(), hotbar.getHeight(),
                hotbar.getWidth(), hotbar.getHeight()
        );
    }

    private static void renderSlot(GuiGraphics graphics, CustomHotbar hotbar, int index) {
        SlotPosition pos = hotbar.getSlotPositions().get(index);
        HotbarSlot slot = hotbar.getSlot(index);

        Identifier slotTexture = slot.getCustomTexture() != null
                ? slot.getCustomTexture()
                : DEFAULT_SLOT_TEXTURE;

        boolean isSelected = index == hotbar.getSelectedSlot();
        if (isSelected) {
            slotTexture = SELECTED_SLOT_TEXTURE;
        }

        float alpha = slot.isEnabled() ? 1.0F : 0.4F;
        //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

        graphics.blit(
                slotTexture,
                pos.x(), pos.y(),
                0, 0,
                pos.width(), pos.height(),
                pos.width(), pos.height()
        );

        //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (slot.hasAbility() && slot.getAbility() != null) {
            int color = isSelected ? 0xFF00FF00 : 0xFFFFFFFF;
            graphics.fill(pos.x() + 2, pos.y() + 2,
                    pos.x() + pos.width() - 2, pos.y() + pos.height() - 2, color);
        } else if (slot.hasItem() && slot.getItemStack() != null) {
            ItemStack stack = slot.getItemStack();
            graphics.renderItem(stack, pos.x() + 3, pos.y() + 3);
            graphics.renderItemDecorations(MC.font, stack, pos.x() + 3, pos.y() + 3);
        } else if (slot.hasCustomData()) {
            int color = 0xFFFFAA00;
            graphics.fill(pos.x() + 4, pos.y() + 4,
                    pos.x() + pos.width() - 4, pos.y() + pos.height() - 4, color);
        }
    }

    private static void renderSelection(GuiGraphics graphics, CustomHotbar hotbar) {
        int selected = hotbar.getSelectedSlot();
        if (selected < 0 || selected >= hotbar.getSlotPositions().size()) return;

        SlotPosition pos = hotbar.getSlotPositions().get(selected);

        int borderColor = 0xFFFFFFFF;
        graphics.hLine(pos.x() - 1, pos.x() + pos.width(), pos.y() - 1, borderColor);
        graphics.hLine(pos.x() - 1, pos.x() + pos.width(), pos.y() + pos.height(), borderColor);
        graphics.vLine(pos.x() - 1, pos.y() - 1, pos.y() + pos.height(), borderColor);
        graphics.vLine(pos.x() + pos.width(), pos.y() - 1, pos.y() + pos.height(), borderColor);
    }
}

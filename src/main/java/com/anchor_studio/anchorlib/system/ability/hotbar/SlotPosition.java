package com.anchor_studio.anchorlib.system.ability.hotbar;

/**
 * Defines the screen position of a single hotbar slot.
 */
public record SlotPosition(int x, int y, int width, int height) {


    public static SlotPosition of(int x, int y) {
        return new SlotPosition(x, y, 18, 18); // Default slot size
    }

    public static SlotPosition of(int x, int y, int size) {
        return new SlotPosition(x, y, size, size);
    }

    public int centerX() {
        return x + width / 2;
    }

    public int centerY() {
        return y + height / 2;
    }

    public boolean contains(int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
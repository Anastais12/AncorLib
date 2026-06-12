package com.anchor_studio.anchorlib.system.ability.hotbar;

import com.anchor_studio.anchorlib.system.ability.Ability;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * A single slot in a CustomHotbar. Can hold either an Ability or ItemStack or custom data.
 */
public class HotbarSlot {

    @Nullable
    private Ability ability;
    @Nullable
    private ItemStack itemStack;
    @Nullable
    private Object customData;
    private boolean enabled = true;
    private Identifier customTexture;

    public HotbarSlot() {}

    public HotbarSlot(@Nullable Ability ability) {
        this.ability = ability;
    }

    public HotbarSlot(@Nullable ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public HotbarSlot(@Nullable Object customData) {
        this.customData = customData;
    }

    @Nullable
    public Ability getAbility() {
        return ability;
    }

    public void setAbility(@Nullable Ability ability) {
        this.ability = ability;
        this.itemStack = null;
        this.customData = null;
    }

    @Nullable
    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(@Nullable ItemStack itemStack) {
        this.itemStack = itemStack;
        this.ability = null;
        this.customData = null;
    }

    @Nullable
    public Object getCustomData() {
        return customData;
    }

    public void setCustomData(@Nullable Object customData) {
        this.customData = customData;
        this.ability = null;
        this.itemStack = null;
    }

    public boolean hasAbility() {
        return ability != null;
    }

    public boolean hasItem() {
        return itemStack != null && !itemStack.isEmpty();
    }

    public boolean hasCustomData() {
        return customData != null;
    }

    public boolean isEmpty() {
        return ability == null && (itemStack == null || itemStack.isEmpty()) && customData == null;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Nullable
    public Identifier getCustomTexture() {
        return customTexture;
    }

    public void setCustomTexture(@Nullable Identifier texture) {
        this.customTexture = texture;
    }

    /**
     * Get display name for this slot
     */
    public String getDisplayName() {
        if (ability != null) return ability.getId().toString();
        if (itemStack != null && !itemStack.isEmpty()) return itemStack.getHoverName().getString();
        if (customData != null) return customData.toString();
        return "Empty";
    }
}
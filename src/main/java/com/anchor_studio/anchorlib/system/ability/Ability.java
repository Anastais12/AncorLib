package com.anchor_studio.anchorlib.system.ability;


import net.minecraft.resources.Identifier;

/**
 * Base class for all custom abilities. NOT items - pure code-based abilities.
 * Mods define abilities by extending this and registering them via AbilityRegistry.
 */
public abstract class Ability {

    private final Identifier id;
    private final Identifier category;
    private boolean enabled = true;

    public Ability(Identifier id, Identifier category) {
        this.id = id;
        this.category = category;
    }

    public Identifier getId() {
        return id;
    }

    public Identifier getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Called when the ability is activated (player presses use key on hotbar slot)
     */
    public abstract void onUse(AbilityContext context);

    /**
     * Called every tick while this ability is selected
     */
    public void onTick(AbilityContext context) {}

    /**
     * Called when the player selects a different slot
     */
    public void onDeselect(AbilityContext context) {}

    /**
     * Called when the player selects this slot
     */
    public void onSelect(AbilityContext context) {}

    /**
     * Cooldown duration in ticks. Return 0 for no cooldown.
     */
    public int getCooldownTicks() {
        return 0;
    }

    /**
     * Whether this ability can be used right now
     */
    public boolean canUse(AbilityContext context) {
        return enabled && !CooldownManager.isOnCooldown(context.player(), id);
    }
}
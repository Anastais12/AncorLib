package com.anchor_studio.anchorlib.system.ability.hotbar;

import com.anchor_studio.anchorlib.system.ability.Ability;
import com.anchor_studio.anchorlib.system.ability.AbilityContext;
import com.anchor_studio.anchorlib.system.ability.CooldownManager;
import com.anchor_studio.anchorlib.system.ability.network.UseAbilityPacket;
import com.anchor_studio.anchorlib.system.ability.data.HotbarData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Base class for custom hotbars. Extend this to create your own hotbar with custom textures,
 * slot layouts, and behavior.
 *
 * Example usage in your mod:
 * <pre>
 * public class MyAbilityHotbar extends CustomHotbar {
 *     public MyAbilityHotbar() {
 *         super(
 *             ResourceLocation.fromNamespaceAndPath("mymod", "textures/gui/hotbar.png"),
 *             new SlotPosition(10, 10),
 *             List.of(
 *                 SlotPosition.of(20, 20),
 *                 SlotPosition.of(45, 20),
 *                 SlotPosition.of(70, 20),
 *                 SlotPosition.of(95, 20),
 *                 SlotPosition.of(120, 20)
 *             )
 *         );
 *         // Assign abilities to slots
 *         setSlot(0, new HotbarSlot(myFireballAbility));
 *         setSlot(1, new HotbarSlot(myHealAbility));
 *     }
 *
 *     {@literal @}Override
 *     public void onSlotUse(int slotIndex, AbilityContext context) {
 *         // Custom logic when a slot is used
 *         if (slotIndex == 4) { // Slot #5
 *             // Special behavior for slot 5
 *         }
 *         super.onSlotUse(slotIndex, context);
 *     }
 * }
 * </pre>
 */
public abstract class CustomHotbar {

    private final Identifier backgroundTexture;
    private final SlotPosition backgroundPosition;
    private final List<SlotPosition> slotPositions;
    private final List<HotbarSlot> slots;
    private int selectedSlot = 0;
    private boolean visible = true;
    private int width;
    private int height;

    // Callbacks
    private Consumer<Integer> onSlotChange;
    private Consumer<Integer> onSlotUse;

    /**
     * @param backgroundTexture The texture for the hotbar background
     * @param backgroundPosition Top-left position of the background texture on screen
     * @param slotPositions List of slot positions relative to screen (not relative to background)
     */
    public CustomHotbar(Identifier backgroundTexture, SlotPosition backgroundPosition,
                        List<SlotPosition> slotPositions) {
        this.backgroundTexture = backgroundTexture;
        this.backgroundPosition = backgroundPosition;
        this.slotPositions = new ArrayList<>(slotPositions);
        this.slots = new ArrayList<>();

        // Initialize empty slots
        for (int i = 0; i < slotPositions.size(); i++) {
            slots.add(new HotbarSlot());
        }

        // Calculate dimensions
        this.width = calculateWidth();
        this.height = calculateHeight();
    }

    /**
     * Convenience constructor with background-relative slot positions
     */
    public CustomHotbar(Identifier backgroundTexture, SlotPosition backgroundPosition,
                        int bgWidth, int bgHeight, List<SlotPosition> relativeSlotPositions) {
        this.backgroundTexture = backgroundTexture;
        this.backgroundPosition = backgroundPosition;
        this.slotPositions = new ArrayList<>();
        this.slots = new ArrayList<>();

        // Convert relative to absolute positions
        for (SlotPosition rel : relativeSlotPositions) {
            this.slotPositions.add(new SlotPosition(
                    backgroundPosition.x() + rel.x(),
                    backgroundPosition.y() + rel.y(),
                    rel.width(),
                    rel.height()
            ));
        }

        for (int i = 0; i < this.slotPositions.size(); i++) {
            slots.add(new HotbarSlot());
        }

        this.width = bgWidth;
        this.height = bgHeight;
    }

    private int calculateWidth() {
        if (slotPositions.isEmpty()) return 0;
        int minX = slotPositions.stream().mapToInt(SlotPosition::x).min().orElse(0);
        int maxX = slotPositions.stream().mapToInt(s -> s.x() + s.width()).max().orElse(0);
        return maxX - minX;
    }

    private int calculateHeight() {
        if (slotPositions.isEmpty()) return 0;
        int minY = slotPositions.stream().mapToInt(SlotPosition::y).min().orElse(0);
        int maxY = slotPositions.stream().mapToInt(s -> s.y() + s.height()).max().orElse(0);
        return maxY - minY;
    }

    // === Slot Management ===

    public void setSlot(int index, HotbarSlot slot) {
        if (index < 0 || index >= slots.size()) {
            throw new IndexOutOfBoundsException("Slot index " + index + " out of bounds for hotbar with " + slots.size() + " slots");
        }
        slots.set(index, slot);
    }

    public HotbarSlot getSlot(int index) {
        return slots.get(index);
    }

    public void setAbility(int index, Ability ability) {
        setSlot(index, new HotbarSlot(ability));
    }

    public void setItem(int index, ItemStack itemStack) {
        setSlot(index, new HotbarSlot(itemStack));
    }

    public void setCustomData(int index, Object data) {
        setSlot(index, new HotbarSlot(data));
    }

    public void clearSlot(int index) {
        setSlot(index, new HotbarSlot());
    }

    public int getSlotCount() {
        return slots.size();
    }

    // === Selection ===

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int slot) {
        if (slot < 0 || slot >= slots.size()) return;

        // Deselect current
        HotbarSlot current = slots.get(selectedSlot);
        if (current.hasAbility() && current.getAbility() != null) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                current.getAbility().onDeselect(AbilityContext.of(mc.player, selectedSlot));
            }
        }

        this.selectedSlot = slot;

        // Select new
        HotbarSlot next = slots.get(slot);
        if (next.hasAbility() && next.getAbility() != null) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                next.getAbility().onSelect(AbilityContext.of(mc.player, slot));
            }
        }

        if (onSlotChange != null) {
            onSlotChange.accept(slot);
        }
    }

    public void selectNextSlot() {
        setSelectedSlot((selectedSlot + 1) % slots.size());
    }

    public void selectPreviousSlot() {
        setSelectedSlot((selectedSlot - 1 + slots.size()) % slots.size());
    }

    /**
     * Use the currently selected slot. Called when player presses use key.
     */
    public void useSelectedSlot() {
        useSlot(selectedSlot);
    }

    /**
     * Use a specific slot by index.
     */
    public void useSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= slots.size()) return;

        HotbarSlot slot = slots.get(slotIndex);
        if (!slot.isEnabled() || slot.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        // Handle abilities
        if (slot.hasAbility() && slot.getAbility() != null) {
            Ability ability = slot.getAbility();
            AbilityContext context = AbilityContext.of(player, slotIndex);

            if (ability.canUse(context)) {
                // Send packet to server for server-side handling
                UseAbilityPacket.send(ability.getId(), slotIndex);

                // Client-side immediate feedback
                onSlotUse(slotIndex, context);

                if (onSlotUse != null) {
                    onSlotUse.accept(slotIndex);
                }
            }
        }
        // Items are handled by vanilla
    }

    /**
     * Override this to add custom behavior when a slot is used.
     * Called on both client and server (via packet).
     */
    public void onSlotUse(int slotIndex, AbilityContext context) {
        // Default: let the ability handle it
        HotbarSlot slot = slots.get(slotIndex);
        if (slot.hasAbility() && slot.getAbility() != null) {
            Ability ability = slot.getAbility();
            ability.onUse(context);

            int cooldown = ability.getCooldownTicks();
            if (cooldown > 0) {
                CooldownManager.setCooldown(context.player(), ability.getId(), cooldown);
            }
        }
    }

    /**
     * Called every client tick for the selected ability
     */
    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        HotbarSlot slot = slots.get(selectedSlot);
        if (slot.hasAbility() && slot.getAbility() != null) {
            slot.getAbility().onTick(AbilityContext.of(mc.player, selectedSlot));
        }
    }

    // === Data Persistence ===

    /**
     * Save this hotbar's data to NBT for persistence
     */
    public HotbarData saveData() {
        HotbarData data = new HotbarData();
        data.setSelectedSlot(selectedSlot);
        data.setSlotCount(slots.size());

        for (int i = 0; i < slots.size(); i++) {
            HotbarSlot slot = slots.get(i);
            if (slot.hasAbility() && slot.getAbility() != null) {
                data.setSlotAbility(i, slot.getAbility().getId());
            } else if (slot.hasItem() && slot.getItemStack() != null) {
                data.setSlotItem(i, slot.getItemStack());
            } else if (slot.hasCustomData()) {
                data.setSlotCustomData(i, slot.getCustomData());
            }
        }

        return data;
    }

    /**
     * Load hotbar data from saved state
     */
    public void loadData(HotbarData data) {
        this.selectedSlot = data.getSelectedSlot();
        // Abilities/items will be resolved by the mod using the registry
    }

    // === Getters ===

    public Identifier getBackgroundTexture() {
        return backgroundTexture;
    }

    public SlotPosition getBackgroundPosition() {
        return backgroundPosition;
    }

    public List<SlotPosition> getSlotPositions() {
        return slotPositions;
    }

    public List<HotbarSlot> getSlots() {
        return slots;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setOnSlotChange(Consumer<Integer> callback) {
        this.onSlotChange = callback;
    }

    public void setOnSlotUse(Consumer<Integer> callback) {
        this.onSlotUse = callback;
    }

    /**
     * Get slot at screen coordinates, or -1 if none
     */
    public int getSlotAt(int mouseX, int mouseY) {
        for (int i = 0; i < slotPositions.size(); i++) {
            if (slotPositions.get(i).contains(mouseX, mouseY)) {
                return i;
            }
        }
        return -1;
    }
}
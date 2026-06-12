package com.anchor_studio.anchorlib.system.ability.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HotbarData {

    public static final Codec<HotbarData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("selected_slot").forGetter(HotbarData::getSelectedSlot),
            Codec.INT.fieldOf("slot_count").forGetter(HotbarData::getSlotCount)
    ).apply(instance, (selected, count) -> {
        HotbarData data = new HotbarData();
        data.selectedSlot = selected;
        data.slotCount = count;
        return data;
    }));

    private int selectedSlot = 0;
    private int slotCount = 0;
    private final Map<Integer, Identifier> slotAbilities = new HashMap<>();
    private final Map<Integer, ItemStack> slotItems = new HashMap<>();
    private final Map<Integer, CompoundTag> slotCustomData = new HashMap<>();

    public int getSelectedSlot() { return selectedSlot; }
    public void setSelectedSlot(int slot) { this.selectedSlot = slot; }

    public int getSlotCount() { return slotCount; }
    public void setSlotCount(int count) { this.slotCount = count; }

    public void setSlotAbility(int index, Identifier abilityId) {
        slotAbilities.put(index, abilityId);
    }

    public Identifier getSlotAbility(int index) {
        return slotAbilities.get(index);
    }

    public void setSlotItem(int index, ItemStack stack) {
        slotItems.put(index, stack.copy());
    }

    public ItemStack getSlotItem(int index) {
        return slotItems.get(index);
    }

    public void setSlotCustomData(int index, Object data) {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", data.getClass().getName());
        tag.putString("value", data.toString());
        slotCustomData.put(index, tag);
    }

    public CompoundTag getSlotCustomData(int index) {
        return slotCustomData.get(index);
    }

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("selected_slot", selectedSlot);
        tag.putInt("slot_count", slotCount);

        ListTag abilities = new ListTag();
        slotAbilities.forEach((idx, id) -> {
            CompoundTag entry = new CompoundTag();
            entry.putInt("index", idx);
            entry.putString("ability", id.toString());
            abilities.add(entry);
        });
        tag.put("abilities", abilities);

        ListTag items = new ListTag();
        slotItems.forEach((idx, stack) -> {
            CompoundTag entry = new CompoundTag();
            entry.putInt("index", idx);
            entry.put("item", Objects.requireNonNull(stack.set(null)));
            items.add(entry);
        });
        tag.put("items", items);

        ListTag custom = new ListTag();
        slotCustomData.forEach((idx, data) -> {
            CompoundTag entry = new CompoundTag();
            entry.putInt("index", idx);
            entry.put("data", data);
            custom.add(entry);
        });
        tag.put("custom", custom);

        return tag;
    }

    public static HotbarData fromNbt(CompoundTag tag) {
        HotbarData data = new HotbarData();

        data.selectedSlot = getInt(tag, "selected_slot");
        data.slotCount = getInt(tag, "slot_count");

        readList(tag, "abilities", entry -> {
            int idx = getInt(entry, "index");
            String abilityStr = getString(entry, "ability");
            if (abilityStr != null && !abilityStr.isEmpty()) {
                data.slotAbilities.put(idx, Identifier.parse(abilityStr));
            }
        });

        ListTag items = tag.getListOrEmpty("items");
        for (int i = 0; i < items.size(); i++) {
            Optional<CompoundTag> entryOpt = items.getCompound(i);
            entryOpt.ifPresent(entry -> {
                int idx = entry.getIntOr("index", 0);
                Optional<CompoundTag> itemTagOpt = entry.getCompound("item");
                itemTagOpt.ifPresent(itemTag -> {
                    ItemStack stack = ItemStack.CODEC.parse(NbtOps.INSTANCE, itemTag).result().orElse(ItemStack.EMPTY);
                    if (stack != null && !stack.isEmpty()) {
                        data.slotItems.put(idx, stack);
                    }
                });
            });
        }

        readList(tag, "custom", entry -> {
            int idx = getInt(entry, "index");
            CompoundTag dataTag = getCompound(entry, "data");
            if (dataTag != null) {
                data.slotCustomData.put(idx, dataTag);
            }
        });

        return data;
    }

    // === Safe NBT getters that handle Optional returns ===

    @SuppressWarnings("unchecked")
    private static int getInt(CompoundTag tag, String key) {
        if (!tag.contains(key)) return 0;
        Object result = tag.get(key);
        if (result instanceof Optional) {
            return ((Optional<Number>) result).map(Number::intValue).orElse(0);
        }
        if (result instanceof Number) {
            return ((Number) result).intValue();
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    private static String getString(CompoundTag tag, String key) {
        if (!tag.contains(key)) return null;
        Object result = tag.get(key);
        if (result instanceof Optional) {
            return ((Optional<String>) result).orElse(null);
        }
        if (result instanceof String) {
            return (String) result;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static CompoundTag getCompound(CompoundTag tag, String key) {
        if (!tag.contains(key)) return null;
        Object result = tag.get(key);
        if (result instanceof Optional) {
            return ((Optional<CompoundTag>) result).orElse(null);
        }
        if (result instanceof CompoundTag) {
            return (CompoundTag) result;
        }
        return null;
    }

    private static void readList(CompoundTag tag, String key, java.util.function.Consumer<CompoundTag> consumer) {
        if (!tag.contains(key)) return;
        try {
            ListTag list = tag.getListOrEmpty(key);
            for (int i = 0; i < list.size(); i++) {
                Tag entry = list.get(i);
                if (entry instanceof CompoundTag) {
                    consumer.accept((CompoundTag) entry);
                }
            }
        } catch (Exception e) {
            // Fallback for different API versions
        }
    }
}

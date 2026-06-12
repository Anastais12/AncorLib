package com.anchor_studio.anchorlib.system.ability.data;

import com.anchor_studio.anchorlib.AnchorLib;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HotbarDataAttachment {

    public static final Codec<Map<String, HotbarData>> HOTBAR_DATA_CODEC =
            Codec.unboundedMap(Codec.STRING, HotbarData.CODEC);

    public static final AttachmentType<Map<String, HotbarData>> HOTBAR_DATA =
            AttachmentRegistry.<Map<String, HotbarData>>builder()
                    .persistent(HOTBAR_DATA_CODEC)
                    .copyOnDeath()
                    .buildAndRegister(Identifier.fromNamespaceAndPath(AnchorLib.MOD_ID, "hotbar_data"));

    public static HotbarData get(net.minecraft.world.entity.player.Player player, String hotbarId) {
        Map<String, HotbarData> map = player.getAttachedOrCreate(HOTBAR_DATA, HashMap::new);
        return map.get(hotbarId);
    }

    public static void set(net.minecraft.world.entity.player.Player player, String hotbarId, HotbarData data) {
        Map<String, HotbarData> map = player.getAttachedOrCreate(HOTBAR_DATA, HashMap::new);
        map.put(hotbarId, data);
        player.setAttached(HOTBAR_DATA, map);
    }
}

package com.anchor_studio.anchorlib.system.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

/**
 * Data-driven ability definition loaded from JSON.
 * Mods define abilities in data/<modid>/anchorlib/abilities/<ability_id>.json
 */
public record AbilityDefinition(
        Identifier id,
        Identifier category,
        String displayName,
        String description,
        int cooldownTicks,
        boolean requiresTarget,
        Identifier iconTexture
) {
    public static final Codec<AbilityDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(AbilityDefinition::id),
            Identifier.CODEC.fieldOf("category").forGetter(AbilityDefinition::category),
            Codec.STRING.fieldOf("display_name").forGetter(AbilityDefinition::displayName),
            Codec.STRING.optionalFieldOf("description", "").forGetter(AbilityDefinition::description),
            Codec.INT.optionalFieldOf("cooldown_ticks", 0).forGetter(AbilityDefinition::cooldownTicks),
            Codec.BOOL.optionalFieldOf("requires_target", false).forGetter(AbilityDefinition::requiresTarget),
            Identifier.CODEC.optionalFieldOf("icon_texture",
                            Identifier.fromNamespaceAndPath("anchorlib", "textures/ability/default.png"))
                    .forGetter(AbilityDefinition::iconTexture)
    ).apply(instance, AbilityDefinition::new));
}
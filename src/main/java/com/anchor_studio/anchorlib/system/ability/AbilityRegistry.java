package com.anchor_studio.anchorlib.system.ability;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Central registry for all abilities. Both library and mod-registered.
 */
public class AbilityRegistry {

    private static final Map<Identifier, Ability> ABILITIES = new HashMap<>();
    private static final Map<Identifier, AbilityDefinition> DEFINITIONS = new HashMap<>();

    public static void register(Ability ability) {
        if (ABILITIES.containsKey(ability.getId())) {
            throw new IllegalStateException("Ability " + ability.getId() + " already registered!");
        }
        ABILITIES.put(ability.getId(), ability);
    }

    public static void registerDefinition(AbilityDefinition definition) {
        DEFINITIONS.put(definition.id(), definition);
    }

    @Nullable
    public static Ability get(Identifier id) {
        return ABILITIES.get(id);
    }

    @Nullable
    public static AbilityDefinition getDefinition(Identifier id) {
        return DEFINITIONS.get(id);
    }

    public static Collection<Ability> getAll() {
        return Collections.unmodifiableCollection(ABILITIES.values());
    }

    public static Collection<AbilityDefinition> getAllDefinitions() {
        return Collections.unmodifiableCollection(DEFINITIONS.values());
    }

    public static boolean has(Identifier id) {
        return ABILITIES.containsKey(id);
    }

    public static void clear() {
        ABILITIES.clear();
        DEFINITIONS.clear();
    }
}
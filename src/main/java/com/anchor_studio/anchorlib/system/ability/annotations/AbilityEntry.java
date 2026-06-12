package com.anchor_studio.anchorlib.system.ability.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as an auto-discoverable ability entry point.
 * The annotated class must extend Ability and have a no-arg constructor.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AbilityEntry {
    String value(); // The ability ID (e.g., "mymod:fireball")
    String category(); // The category ID (e.g., "mymod:combat")
    String displayName(); // Human-readable name
}
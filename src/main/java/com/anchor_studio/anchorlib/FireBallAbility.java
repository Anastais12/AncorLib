package com.anchor_studio.anchorlib;

import com.anchor_studio.anchorlib.system.ability.Ability;
import com.anchor_studio.anchorlib.system.ability.AbilityContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.phys.Vec3;

public class FireBallAbility extends Ability {

    public FireBallAbility() {
        super(
                Identifier.fromNamespaceAndPath("mymod", "fireball"),
                Identifier.fromNamespaceAndPath("mymod", "combat")
        );
    }

    @Override
    public void onUse(AbilityContext context) {
        // Always check client side - abilities execute on server
        if (context.isClientSide()) return;

        Vec3 look = context.player().getLookAngle();

        LargeFireball fireball = new LargeFireball(
                context.level(),
                context.player(),
                look,
                1
        );

        Vec3 eyePos = context.player().getEyePosition();

        fireball.setPos(
                eyePos.x + look.x,
                eyePos.y + look.y,
                eyePos.z + look.z
        );

        context.level().addFreshEntity(fireball);
        fireball.setPos(context.player().getEyePosition().add(look.scale(1.5)));
        context.level().addFreshEntity(fireball);
    }

    @Override
    public int getCooldownTicks() {
        return 60; // 3 seconds at 20 TPS
    }
}

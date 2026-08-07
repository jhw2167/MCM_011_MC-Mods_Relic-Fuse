package com.holybuckets.relicfuse.effect;

import com.holybuckets.relicfuse.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

public class AncientPowerEffect extends MobEffect {

    public static final int COLOR = 0xC9A227;

    public AncientPowerEffect() {
        super(MobEffectCategory.BENEFICIAL, COLOR);
    }

    @Override
    public ParticleOptions createParticleOptions(MobEffectInstance instance) {
        return ModParticles.ancientPower.value();
    }

}

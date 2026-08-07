package com.holybuckets.relicfuse.particle;

import net.blay09.mods.balm.core.particles.BalmParticleTypeRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.SimpleParticleType;

public class ModParticles {

    public static Holder<SimpleParticleType> ancientPower;

    public static void initialize(BalmParticleTypeRegistrar particleTypes) {
        ancientPower = particleTypes.register("ancient_power", false).asHolder();
    }

}

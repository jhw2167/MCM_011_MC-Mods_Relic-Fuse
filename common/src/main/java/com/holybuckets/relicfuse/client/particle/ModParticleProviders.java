package com.holybuckets.relicfuse.client.particle;

import com.holybuckets.relicfuse.particle.ModParticles;
import net.blay09.mods.balm.client.particle.BalmParticleProviderRegistrar;

public class ModParticleProviders {

    public static void initialize(BalmParticleProviderRegistrar particleProviders) {
        particleProviders.register(ModParticles.ancientPower, AncientPowerParticle.Provider::new);
    }

}

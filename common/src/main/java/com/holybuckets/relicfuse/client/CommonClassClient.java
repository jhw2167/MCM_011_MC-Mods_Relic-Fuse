package com.holybuckets.relicfuse.client;

import com.holybuckets.relicfuse.client.particle.ModParticleProviders;
import net.blay09.mods.balm.client.BalmClientRegistrars;


public class CommonClassClient {

    public static void initClient(BalmClientRegistrars registrars) {
        registrars.particleProviders(ModParticleProviders::initialize);
    }

    public static void sample()
    {

    }


}

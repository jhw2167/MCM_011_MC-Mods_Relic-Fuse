package com.holybuckets.relicfuse.client;

import com.holybuckets.foundation.client.ClientEventRegistrar;
import com.holybuckets.relicfuse.client.particle.ModParticleProviders;
import net.blay09.mods.balm.client.BalmClientRegistrars;


public class CommonClassClient {

    public static void initClient(BalmClientRegistrars registrars) {
        registrars.particleProviders(ModParticleProviders::initialize);
        FusionClientManager.init(ClientEventRegistrar.getInstance());
    }

    public static void sample()
    {

    }


}

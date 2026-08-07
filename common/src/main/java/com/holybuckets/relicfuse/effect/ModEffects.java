package com.holybuckets.relicfuse.effect;

import com.holybuckets.foundation.effect.EffectRegistry;
import com.holybuckets.relicfuse.Constants;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

public class ModEffects {

    public static final String ANCIENT_POWER = "ancient_power";

    public static Holder<MobEffect> ancientPower;

    public static void register(BalmRegistrar registrar) {
        ancientPower = EffectRegistry.registerEffect(registrar, Constants.MOD_ID, ANCIENT_POWER, AncientPowerEffect::new);
    }

}

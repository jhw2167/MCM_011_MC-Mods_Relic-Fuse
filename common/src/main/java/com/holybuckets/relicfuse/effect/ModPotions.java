package com.holybuckets.relicfuse.effect;

import com.holybuckets.foundation.effect.EffectRegistry;
import com.holybuckets.relicfuse.Constants;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.world.item.alchemy.Potion;

public class ModPotions {

    public static final int ANCIENT_POWER_DURATION = 3600;

    public static Holder<Potion> ancientPower;

    public static void register(BalmRegistrar registrar) {
        ancientPower = EffectRegistry.registerPotion(registrar, Constants.MOD_ID, ModEffects.ANCIENT_POWER,
            ModEffects.ancientPower, ANCIENT_POWER_DURATION, 0);
    }

}

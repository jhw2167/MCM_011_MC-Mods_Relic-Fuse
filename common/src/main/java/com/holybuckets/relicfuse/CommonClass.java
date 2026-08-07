package com.holybuckets.relicfuse;

import com.holybuckets.relicfuse.block.ModBlocks;
import com.holybuckets.relicfuse.item.ModItems;
import com.holybuckets.relicfuse.platform.Services;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;


public class CommonClass {

    public static boolean isInitialized = false;

    public static void init(BalmRegistrars registrars)
    {
        if (isInitialized)
            return;

        Constants.LOG.info("Hello from Common init on {}! we are currently in a {} environment!", Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());
        Constants.LOG.info("The ID for diamonds is {}", BuiltInRegistries.ITEM.getKey(Items.DIAMOND));

        com.holybuckets.foundation.FoundationInitializers.commonInitialize();

        if (Services.PLATFORM.isModLoaded(Constants.MOD_ID)) {
            Constants.LOG.info("Hello to " + Constants.MOD_NAME + "!");
        }

        initRegistries(registrars);

        RelicFuseMain.INSTANCE = new RelicFuseMain();

        isInitialized = true;
    }

    private static void initRegistries(BalmRegistrars registrars) {
        registrars.blocks(ModBlocks::initialize);
        registrars.items(ModItems::initialize);
        registrars.creativeModeTabs(ModItems::creativeTab);
    }

    public static void sample()
    {

    }
}

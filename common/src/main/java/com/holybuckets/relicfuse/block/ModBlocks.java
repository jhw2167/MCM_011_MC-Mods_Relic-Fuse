package com.holybuckets.relicfuse.block;

import net.blay09.mods.balm.world.level.block.BalmBlockRegistrar;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public static void initialize(BalmBlockRegistrar blocks) {
    }

    private static BlockBehaviour.Properties defaultProperties() {
        return BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(5f, 2000f);
    }

}

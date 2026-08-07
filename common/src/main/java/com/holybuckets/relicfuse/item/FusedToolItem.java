package com.holybuckets.relicfuse.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class FusedToolItem extends Item implements IFusedTool {

    public FusedToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFused(ItemStack stack) {
        return false;
    }
}

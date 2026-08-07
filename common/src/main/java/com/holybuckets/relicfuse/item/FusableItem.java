package com.holybuckets.relicfuse.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class FusableItem extends Item implements IFusableItem {

    public FusableItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack asFusionIngredient(ItemStack stack) {
        return ItemStack.EMPTY;
    }
}

package com.holybuckets.relicfuse.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class FusableItem extends Item implements IFusableItem {

    public FusableItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public ItemStack asFusionIngredient(ItemStack stack) {
        return ItemStack.EMPTY;
    }

}

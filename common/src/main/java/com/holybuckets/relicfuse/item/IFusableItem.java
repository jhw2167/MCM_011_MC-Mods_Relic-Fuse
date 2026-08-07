package com.holybuckets.relicfuse.item;

import net.minecraft.world.item.ItemStack;

public interface IFusableItem {

    ItemStack asFusionIngredient(ItemStack stack);
}

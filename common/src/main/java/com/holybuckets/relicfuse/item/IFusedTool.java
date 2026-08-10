package com.holybuckets.relicfuse.item;

import com.holybuckets.relicfuse.component.FusionComponent;
import net.minecraft.world.item.ItemStack;

public interface IFusedTool {

    default boolean isFused(ItemStack stack) {
        return FusionComponent.isFused(stack);
    }


}

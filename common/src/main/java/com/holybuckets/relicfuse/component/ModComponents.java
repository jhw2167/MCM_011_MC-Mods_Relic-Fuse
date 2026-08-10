package com.holybuckets.relicfuse.component;

import net.blay09.mods.balm.core.BalmRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

public class ModComponents {

    public static Holder<DataComponentType<?>> fusionComponent;

    public static void register(BalmRegistrar registrar) {
        ResourceKey<DataComponentType<?>> key = ResourceKey.create(
            Registries.DATA_COMPONENT_TYPE, FusionComponent.LOC);

        fusionComponent = registrar.register(key, loc -> FusionComponent.TYPE);
    }

}

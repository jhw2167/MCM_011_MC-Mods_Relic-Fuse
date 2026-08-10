package com.holybuckets.relicfuse.component;

import com.holybuckets.relicfuse.Constants;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Records which modifier item was fused into a tool. The id is resolved back to the modifier
 * Item at behavior time rather than storing a direct reference.
 */
public class FusionComponent {

    public static final Identifier LOC = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "fusion");

    public static final Codec<FusionComponent> CODEC = Identifier.CODEC
        .xmap(FusionComponent::new, c -> c.modifierId);

    public static final DataComponentType<FusionComponent> TYPE = DataComponentType
        .<FusionComponent>builder()
        .persistent(CODEC)
        .build();

    private final Identifier modifierId;

    public FusionComponent(Identifier modifierId) {
        this.modifierId = modifierId;
    }

    public Identifier getModifierId() {
        return modifierId;
    }

    public Item getModifier() {
        return BuiltInRegistries.ITEM.getOptional(modifierId).orElse(null);
    }

    public static void apply(ItemStack tool, Item modifier) {
        tool.set(TYPE, new FusionComponent(BuiltInRegistries.ITEM.getKey(modifier)));
    }

    public static boolean isFused(ItemStack stack) {
        return stack.has(TYPE);
    }

    public static Item getModifier(ItemStack stack) {
        FusionComponent component = stack.get(TYPE);
        return component == null ? null : component.getModifier();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return modifierId.equals(((FusionComponent) o).modifierId);
    }

    @Override
    public int hashCode() {
        return modifierId.hashCode();
    }

}

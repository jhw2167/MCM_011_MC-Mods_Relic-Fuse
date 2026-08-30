package com.holybuckets.relicfuse.core;

import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.balm.server.ServerStartingEvent;
import com.holybuckets.relicfuse.component.FusionComponent;
import com.holybuckets.relicfuse.item.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Brush tier chain. Upgrading swaps the item for the next tier and carries the existing component
 * patch across, so a brush that has already been crystal or bone fused keeps that fusion.
 */
public class FusionBrushTiers {


    static final Map<Item, Item> BRUSH_UPGRADES = new LinkedHashMap<>();

    public static void init(EventRegistrar reg) {
        reg.registerOnBeforeServerStarted(FusionBrushTiers::onBeforeServerStarted);
    }

    public static boolean hasTier(Item item) {
        return BRUSH_UPGRADES.containsKey(item);
    }

    private static void onBeforeServerStarted(ServerStartingEvent event) {
        BRUSH_UPGRADES.clear();
        BRUSH_UPGRADES.put(Items.IRON_INGOT, ModItems.ironBrush.get());
        BRUSH_UPGRADES.put(Items.GOLD_INGOT, ModItems.goldBrush.get());
        BRUSH_UPGRADES.put(Items.DIAMOND, ModItems.diamondBrush.get());
        BRUSH_UPGRADES.put(Items.NETHERITE_INGOT, ModItems.netheriteBrush.get());

        BRUSH_UPGRADES.put(ModItems.demonicCrystal.get(), ModItems.ultimateBrush.get());
        BRUSH_UPGRADES.put(ModItems.spiritedBone.get(), ModItems.spiritedBrush.get());
        BRUSH_UPGRADES.put(ModItems.blessedCrystal.get(), ModItems.spiritedBrush.get());
    }

}

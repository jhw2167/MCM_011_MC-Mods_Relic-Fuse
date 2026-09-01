package com.holybuckets.relicfuse.core;

import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.balm.server.ServerStartingEvent;
import com.holybuckets.relicfuse.item.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FusionStats {

    private static final float CRYSTAL_DURABILITY_MODIFIER = 0.5f; //multiplied by durability to get new durability
    private static final float BONE_DURABILITY_MODIFIER = 1f; //multiplied by durability to get new durability


    public static void init(EventRegistrar registrar) {
        registrar.registerOnBeforeServerStarted(FusionStats::onBeforeServerStarted);
    }

    public static void onBeforeServerStarted(ServerStartingEvent event)
    {
        TOOL_UPDATES = new HashMap<>();
        TOOL_UPDATES.put(ModItems.blessedCrystal.get(), BlessedCrystal.register);
        TOOL_UPDATES.put(ModItems.demonicCrystal.get(), DemonicCrystal.register);
        TOOL_UPDATES.put(ModItems.earthCrystal.get(), EarthCrystal.register);
        TOOL_UPDATES.put(ModItems.toxicCrystal.get(), ToxicCrystal.register);
        TOOL_UPDATES.put(ModItems.electricCrystal.get(), LightningCrystal.register);

        //tool updates for bones
        TOOL_UPDATES.put(ModItems.encasedBone.get(), EncasedBone.register);
        TOOL_UPDATES.put(ModItems.overgrownBone.get(), OvergrownBone.register);
        TOOL_UPDATES.put(ModItems.spiritedBone.get(), SpiritedBone.register);
        TOOL_UPDATES.put(ModItems.toxicBone.get(), ToxicBone.register);
        TOOL_UPDATES.put(ModItems.enderBone.get(), EnderBone.register);

        //powders -- not needed




    }

    private static Map<Item, Consumer<ItemStack>> TOOL_UPDATES;
    public static void initFusedItem(ItemStack fused, ItemStack modifier)
    {
        if(fused.has(DataComponents.TOOL)) {
            if(TOOL_UPDATES.containsKey(modifier.getItem())) {
                TOOL_UPDATES.get(modifier.getItem()).accept(fused);
            }
        }


        if(fused.getItem() instanceof AxeItem) {
            //axes
        } else if (fused.getItem() instanceof HoeItem) {

        } else if(fused.getItemName().getString().toLowerCase().contains("pickaxe")) {
            //pickaxes
        } else if(fused.getItemName().getString().toLowerCase().contains("sword")) {

        } else if(fused.getItem() instanceof ShovelItem) {
            //shovels
        }
        else if(fused.getItem().equals(Items.BRUSH)) {
            //brushes
        }

    }

    public static class BlessedCrystal {

        static Consumer<ItemStack> register = (sword) -> {
            updateDurability(sword, CRYSTAL_DURABILITY_MODIFIER);
        };
    }

    //demonic crystal - add 2 levels to all enchants on item
    public static class DemonicCrystal {

        static Consumer<ItemStack> register = (sword) -> {
            onFuseTool(null, sword);
            updateDurability(sword, CRYSTAL_DURABILITY_MODIFIER);
        };

        public static void onFuseTool(@Nullable ServerPlayer p, ItemStack sword)
        {
            //add 2 levels to all enchants on item
            sword.getEnchantments().keySet().forEach(enchantment -> {
                int currentLevel = EnchantmentHelper.getItemEnchantmentLevel(enchantment, sword);
                int newLevel = currentLevel + 2;
                sword.enchant(enchantment, newLevel);
            });
        }
    }

    //earth crystal
    public static class EarthCrystal {
        static Consumer<ItemStack> register = (sword) -> {
            updateDurability(sword, CRYSTAL_DURABILITY_MODIFIER);
        };
    }

    //Toxic crystal
    public static class ToxicCrystal {
        static Consumer<ItemStack> register = (sword) -> {
            updateDurability(sword, CRYSTAL_DURABILITY_MODIFIER);
        };
    }

    //Lightning crystal
    public static class LightningCrystal {
        static Consumer<ItemStack> register = (sword) -> {
            updateDurability(sword, CRYSTAL_DURABILITY_MODIFIER);
        };
    }

    //Encased Bone
    public static class EncasedBone {
        static Consumer<ItemStack> register = (sword) -> {
            //updateDurability(sword, BONE_DURABILITY_MODIFIER);
            //add 2 levels of unbreaking
            HBUtil.ItemUtil.addEnchant(sword, Enchantments.UNBREAKING, 2);
        };
    }

    //Overgrown Bone
    public static class OvergrownBone {
        static Consumer<ItemStack> register = (sword) -> {
            updateDurability(sword, BONE_DURABILITY_MODIFIER);
            //HBUtil.ItemUtil.addEnchant(sword, Enchantments.FORTUNE, 1);
        };
    }

    //Spirited Bone
    public static class SpiritedBone {
        static Consumer<ItemStack> register = (sword) -> {
            updateDurability(sword, BONE_DURABILITY_MODIFIER);
            //HBUtil.ItemUtil.addEnchant(sword, Enchantments.EFFICIENCY, 1);
        };
    }

    //Toxic Bone
    public static class ToxicBone {
        static Consumer<ItemStack> register = (sword) -> {
            updateDurability(sword, BONE_DURABILITY_MODIFIER);
            //HBUtil.ItemUtil.removeEnchant(sword, Enchantments.MENDING);
        };
    }

    //Ender Bone
    public static class EnderBone {
        static Consumer<ItemStack> register = (sword) -> {
            updateDurability(sword, BONE_DURABILITY_MODIFIER);
            HBUtil.ItemUtil.removeEnchant(sword, Enchantments.VANISHING_CURSE);
        };
    }

    //** UTILITY **/

    private static void updateDurability(ItemStack sword, float modifier) {
        int currentDurability = sword.getMaxDamage() - sword.getDamageValue();
        int newDurability = (int) (currentDurability * modifier);
        sword.setDamageValue(sword.getMaxDamage() - newDurability);
        //also need to reduce tools max durability
        int newMaxDurability = (int) (sword.getMaxDamage() * modifier);
        //this is the current durability, we need to set new max

    }

}

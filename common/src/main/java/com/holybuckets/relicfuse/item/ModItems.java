package com.holybuckets.relicfuse.item;

import com.holybuckets.foundation.item.CreativeTabRegistry;
import com.holybuckets.foundation.util.DeferredObject;
import com.holybuckets.relicfuse.Constants;
import com.holybuckets.relicfuse.item.fusable.BoneItem;
import com.holybuckets.relicfuse.item.fusable.CrystalItem;
import com.holybuckets.relicfuse.item.tool.BrushItem;
import com.holybuckets.relicfuse.item.tool.FusedAxeItem;
import com.holybuckets.relicfuse.item.tool.FusedHoeItem;
import com.holybuckets.relicfuse.item.tool.FusedPickaxeItem;
import com.holybuckets.relicfuse.item.tool.FusedShovelItem;
import com.holybuckets.relicfuse.item.tool.FusedSwordItem;
import com.holybuckets.relicfuse.item.tool.GloveItem;
import com.holybuckets.relicfuse.item.tool.ThunderHammerItem;
import net.blay09.mods.balm.world.item.BalmCreativeModeTabRegistrar;
import net.blay09.mods.balm.world.item.BalmItemRegistrar;
import net.blay09.mods.balm.world.item.DeferredItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;

import java.util.ArrayList;
import java.util.List;

public class ModItems {

    private static final List<DeferredObject<Item>> ITEMS = new ArrayList<>();

    public static DeferredItem ironBrushItem;
    public static DeferredItem diamondBrushItem;
    public static DeferredItem netheriteBrushItem;
    public static DeferredItem blazeForgedBrushItem;
    public static DeferredItem earthHarvesterBrushItem;
    public static DeferredItem spiritedBrushItem;
    public static DeferredItem ultimateBrushItem;

    public static DeferredItem blessedCrystalItem;
    public static DeferredItem demonicCrystalItem;
    public static DeferredItem earthCrystalItem;
    public static DeferredItem electricCrystalItem;
    public static DeferredItem toxicCrystalItem;

    public static DeferredItem encasedBoneItem;
    public static DeferredItem overgrownBoneItem;
    public static DeferredItem spiritedBoneItem;
    public static DeferredItem toxicBoneItem;
    public static DeferredItem enderBoneItem;

    public static DeferredItem archeologyAxeItem;
    public static DeferredItem archeologyHoeItem;
    public static DeferredItem archeologyPickaxeItem;
    public static DeferredItem archeologyShovelItem;
    public static DeferredItem archeologySwordItem;

    public static DeferredItem rapidStrikeGloveItem;
    public static DeferredItem singleStrikeGloveItem;
    public static DeferredItem thunderHammerItem;

    public static DeferredObject<Item> ironBrush;
    public static DeferredObject<Item> diamondBrush;
    public static DeferredObject<Item> netheriteBrush;
    public static DeferredObject<Item> blazeForgedBrush;
    public static DeferredObject<Item> earthHarvesterBrush;
    public static DeferredObject<Item> spiritedBrush;
    public static DeferredObject<Item> ultimateBrush;

    public static DeferredObject<Item> blessedCrystal;
    public static DeferredObject<Item> demonicCrystal;
    public static DeferredObject<Item> earthCrystal;
    public static DeferredObject<Item> electricCrystal;
    public static DeferredObject<Item> toxicCrystal;

    public static DeferredObject<Item> encasedBone;
    public static DeferredObject<Item> overgrownBone;
    public static DeferredObject<Item> spiritedBone;
    public static DeferredObject<Item> toxicBone;
    public static DeferredObject<Item> enderBone;

    public static DeferredObject<Item> archeologyAxe;
    public static DeferredObject<Item> archeologyHoe;
    public static DeferredObject<Item> archeologyPickaxe;
    public static DeferredObject<Item> archeologyShovel;
    public static DeferredObject<Item> archeologySword;

    public static DeferredObject<Item> rapidStrikeGlove;
    public static DeferredObject<Item> singleStrikeGlove;
    public static DeferredObject<Item> thunderHammer;

    public static void initialize(BalmItemRegistrar items) {
        ITEMS.clear();

        ironBrushItem = items.register("iron_brush", BrushItem::new, props -> props.durability(64)).asDeferredItem();
        ironBrush = track(ironBrushItem);

        diamondBrushItem = items.register("diamond_brush", BrushItem::new, props -> props.durability(128)).asDeferredItem();
        diamondBrush = track(diamondBrushItem);

        netheriteBrushItem = items.register("netherite_brush", BrushItem::new, props -> props.durability(256)).asDeferredItem();
        netheriteBrush = track(netheriteBrushItem);

        blazeForgedBrushItem = items.register("blaze_forged_brush", BrushItem::new, props -> props.durability(256)).asDeferredItem();
        blazeForgedBrush = track(blazeForgedBrushItem);

        earthHarvesterBrushItem = items.register("earth_harvester_brush", BrushItem::new, props -> props.durability(256)).asDeferredItem();
        earthHarvesterBrush = track(earthHarvesterBrushItem);

        spiritedBrushItem = items.register("spirited_brush", BrushItem::new, props -> props.durability(256)).asDeferredItem();
        spiritedBrush = track(spiritedBrushItem);

        ultimateBrushItem = items.register("ultimate_brush", BrushItem::new, props -> props.durability(512)).asDeferredItem();
        ultimateBrush = track(ultimateBrushItem);

        blessedCrystalItem = items.register("blessed_crystal", CrystalItem::new).asDeferredItem();
        blessedCrystal = track(blessedCrystalItem);

        demonicCrystalItem = items.register("demonic_crystal", CrystalItem::new).asDeferredItem();
        demonicCrystal = track(demonicCrystalItem);

        earthCrystalItem = items.register("earth_crystal", CrystalItem::new).asDeferredItem();
        earthCrystal = track(earthCrystalItem);

        electricCrystalItem = items.register("electric_crystal", CrystalItem::new).asDeferredItem();
        electricCrystal = track(electricCrystalItem);

        toxicCrystalItem = items.register("toxic_crystal", CrystalItem::new).asDeferredItem();
        toxicCrystal = track(toxicCrystalItem);

        encasedBoneItem = items.register("encased_bone", BoneItem::new).asDeferredItem();
        encasedBone = track(encasedBoneItem);

        overgrownBoneItem = items.register("overgrown_bone", BoneItem::new).asDeferredItem();
        overgrownBone = track(overgrownBoneItem);

        spiritedBoneItem = items.register("spirited_bone", BoneItem::new).asDeferredItem();
        spiritedBone = track(spiritedBoneItem);

        toxicBoneItem = items.register("toxic_bone", BoneItem::new).asDeferredItem();
        toxicBone = track(toxicBoneItem);

        enderBoneItem = items.register("ender_bone", BoneItem::new).asDeferredItem();
        enderBone = track(enderBoneItem);

        archeologyAxeItem = items.register("archeology_axe", FusedAxeItem::new, props -> props.axe(ToolMaterial.IRON, 6f, -3.1f)).asDeferredItem();
        archeologyAxe = track(archeologyAxeItem);

        archeologyHoeItem = items.register("archeology_hoe", FusedHoeItem::new, props -> props.hoe(ToolMaterial.IRON, 0f, -3f)).asDeferredItem();
        archeologyHoe = track(archeologyHoeItem);

        archeologyPickaxeItem = items.register("archeology_pickaxe", FusedPickaxeItem::new, props -> props.pickaxe(ToolMaterial.IRON, 1f, -2.8f)).asDeferredItem();
        archeologyPickaxe = track(archeologyPickaxeItem);

        archeologyShovelItem = items.register("archeology_shovel", FusedShovelItem::new, props -> props.shovel(ToolMaterial.IRON, 1.5f, -3f)).asDeferredItem();
        archeologyShovel = track(archeologyShovelItem);

        archeologySwordItem = items.register("archeology_sword", FusedSwordItem::new, props -> props.sword(ToolMaterial.IRON, 3f, -2.4f)).asDeferredItem();
        archeologySword = track(archeologySwordItem);

        rapidStrikeGloveItem = items.register("rapid_strike_glove", GloveItem::new, props -> props.durability(256)).asDeferredItem();
        rapidStrikeGlove = track(rapidStrikeGloveItem);

        singleStrikeGloveItem = items.register("single_strike_glove", GloveItem::new, props -> props.durability(256)).asDeferredItem();
        singleStrikeGlove = track(singleStrikeGloveItem);

        thunderHammerItem = items.register("thunder_hammer", ThunderHammerItem::new, props -> props.durability(512)).asDeferredItem();
        thunderHammer = track(thunderHammerItem);
    }

    /*CreativeModeTab.DisplayItemsGenerator generator = (parameters, output) -> {
        for (DeferredObject<Item> item : DISPLAY_ORDER) {
            output.accept(item.get());
        }
    }*/

    public static void creativeTab(BalmCreativeModeTabRegistrar tabRegistrar) {
        CreativeTabRegistry.registerTab(tabRegistrar, Constants.MOD_ID, ModItems.ironBrushItem, ITEMS);
    }

    private static DeferredObject<Item> track(DeferredItem item) {
        DeferredObject<Item> deferred = DeferredObject.of(item);
        ITEMS.add(deferred);
        return deferred;
    }

}

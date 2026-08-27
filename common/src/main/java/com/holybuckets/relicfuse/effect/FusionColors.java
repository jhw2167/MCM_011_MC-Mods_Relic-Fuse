package com.holybuckets.relicfuse.effect;

import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.balm.server.ServerStartingEvent;
import com.holybuckets.relicfuse.item.ModItems;
import com.holybuckets.foundation.util.DeferredObject;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * Item to particle colour table for the fusion glow, mirroring the wool-dust cache in
 * HBs-Satellites (WoolColorHelper). Colours were sampled from each modifier's own sprite and then
 * hand-tuned, so edit the hex values freely; the DustParticleOptions cache is rebuilt from them.
 *
 * Built on server start because ModItems fields are only populated once item registration has run.
 */
public class FusionColors {

    /** Particle size passed to DustParticleOptions. 1.0f matches vanilla redstone dust. */
    public static float DUST_SCALE = 0.9f;

    /* CRYSTALS */
    public static int BLESSED_CRYSTAL   = 0xF2E9A0; // pale gold
    public static int DEMONIC_CRYSTAL   = 0xC3123A; // crimson
    public static int EARTH_CRYSTAL     = 0x46FF74; // spring green
    public static int ELECTRIC_CRYSTAL  = 0x53EAF7; // arc cyan
    public static int TOXIC_CRYSTAL     = 0x8CE01E; // acid green

    /* BONES */
    public static int ENCASED_BONE      = 0xA16E52; // earthen brown
    public static int OVERGROWN_BONE    = 0x6FBF4A; // moss green
    public static int SPIRITED_BONE     = 0x41F384; // spectral green
    public static int TOXIC_BONE        = 0xA2F41D; // sickly yellow-green
    public static int ENDER_BONE        = 0x2CCDB1; // ender teal

    private static final Map<Item, Integer> COLORS = new HashMap<>();
    private static final Map<Item, DustParticleOptions> DUST = new HashMap<>();

    public static void init(EventRegistrar reg) {
        reg.registerOnBeforeServerStarted(FusionColors::onServerStarting);
    }

    private static void onServerStarting(ServerStartingEvent event) {
        COLORS.clear();
        DUST.clear();

        put(ModItems.blessedCrystal, BLESSED_CRYSTAL);
        put(ModItems.demonicCrystal, DEMONIC_CRYSTAL);
        put(ModItems.earthCrystal, EARTH_CRYSTAL);
        put(ModItems.electricCrystal, ELECTRIC_CRYSTAL);
        put(ModItems.toxicCrystal, TOXIC_CRYSTAL);

        put(ModItems.encasedBone, ENCASED_BONE);
        put(ModItems.overgrownBone, OVERGROWN_BONE);
        put(ModItems.spiritedBone, SPIRITED_BONE);
        put(ModItems.toxicBone, TOXIC_BONE);
        put(ModItems.enderBone, ENDER_BONE);
    }

    private static void put(DeferredObject<Item> item, int rgb) {
        if (item == null || item.get() == null) return;
        COLORS.put(item.get(), rgb);
        DUST.put(item.get(), new DustParticleOptions(rgb, DUST_SCALE));
    }

    public static Integer getColor(Item modifier) {
        return modifier == null ? null : COLORS.get(modifier);
    }
    public static DustParticleOptions getDust(Item modifier) {
        return modifier == null ? null : DUST.get(modifier);
    }

    public static boolean hasColor(Item modifier) {
        return modifier != null && DUST.containsKey(modifier);
    }

}

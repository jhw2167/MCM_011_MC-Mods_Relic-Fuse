package com.holybuckets.relicfuse.core;

import com.holybuckets.foundation.GeneralConfig;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.balm.server.ServerStartingEvent;
import com.holybuckets.relicfuse.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Rolls a fusable relic to substitute into an archaeology dig. Final odds are the item's rareness
 * plus the bonus of the brush in use; anything at or below zero never enters the table.
 */
public class FusionItemWeights {

    public enum Rareness {
        COMMON,
        UNCOMMON,
        RARE,
        EPIC,
        LEGENDARY
    }

    public static final int[] RARENESS_ODDS = { 10, 5, 0, -5, -10 };

    public static final int TABLE_SIZE = 100;
    public static final int OVERFLOW_RANGE = 200;

    private static final Item[] RELICS = new Item[TABLE_SIZE];
    private static final Map<Item, Rareness> RELIC_RARENESS = new LinkedHashMap<>();
    private static final Map<Item, Integer> BRUSH_BONUS = new LinkedHashMap<>();

    private static Random RANDOM;

    public static void init(EventRegistrar reg) {
        reg.registerOnBeforeServerStarted(FusionItemWeights::onBeforeServerStarted);
    }

    private static void onBeforeServerStarted(ServerStartingEvent event) {
        GeneralConfig config = GeneralConfig.getInstance();
        RANDOM = new Random(config.getWorldSeed() * (config.getTotalTickCount() + 1));
        loadBrushBonuses();
        loadRelicRareness();
    }

    private static void loadBrushBonuses() {
        BRUSH_BONUS.clear();
        BRUSH_BONUS.put(Items.BRUSH, 0);
        BRUSH_BONUS.put(ModItems.ironBrush.get(), 5);
        BRUSH_BONUS.put(ModItems.goldBrush.get(), 7); //fast but fragile
        BRUSH_BONUS.put(ModItems.diamondBrush.get(), 8);
        BRUSH_BONUS.put(ModItems.netheriteBrush.get(), 12);

        BRUSH_BONUS.put(ModItems.earthHarvesterBrush.get(), 15); //only yields bones
        BRUSH_BONUS.put(ModItems.spiritedBrush.get(), 15);  //only yields crystals

        BRUSH_BONUS.put(ModItems.ultimateBrush.get(), 20);

        BRUSH_BONUS.put(ModItems.blazeForgedBrush.get(), 12);
    }

    private static void loadRelicRareness() {
        RELIC_RARENESS.clear();

        RELIC_RARENESS.put(ModItems.ancientTotem.get(), Rareness.UNCOMMON);

        RELIC_RARENESS.put(ModItems.encasedBone.get(), Rareness.RARE);
        RELIC_RARENESS.put(ModItems.overgrownBone.get(), Rareness.RARE);
        RELIC_RARENESS.put(ModItems.spiritedBone.get(), Rareness.EPIC);
        RELIC_RARENESS.put(ModItems.toxicBone.get(), Rareness.RARE);
        RELIC_RARENESS.put(ModItems.enderBone.get(), Rareness.EPIC);

        RELIC_RARENESS.put(ModItems.earthCrystal.get(), Rareness.EPIC);
        RELIC_RARENESS.put(ModItems.blessedCrystal.get(), Rareness.EPIC);
        RELIC_RARENESS.put(ModItems.demonicCrystal.get(), Rareness.LEGENDARY);
        RELIC_RARENESS.put(ModItems.toxicCrystal.get(), Rareness.EPIC);
        RELIC_RARENESS.put(ModItems.electricCrystal.get(), Rareness.LEGENDARY);
    }


    public static int brushBonus(@Nullable Item brush) {
        if (brush == null) return 0;
        Integer bonus = BRUSH_BONUS.get(brush);
        return bonus == null ? 0 : bonus;
    }

    public static boolean isBrush(@Nullable Item item) {
        return item != null && BRUSH_BONUS.containsKey(item);
    }

    @Nullable
    public static Item tryRelic(ServerLevel level, @Nullable LivingEntity digger, @Nullable ItemInstance tool) {
        if (RANDOM == null || RELIC_RARENESS.isEmpty()) return null;

        Item brush = resolveBrush(digger, tool);
        if (!isBrush(brush)) return null;

        buildTable(brushBonus(brush));
        return RELICS[RANDOM.nextInt(TABLE_SIZE)];
    }

    @Nullable
    private static Item resolveBrush(@Nullable LivingEntity digger, @Nullable ItemInstance tool) {
        if (tool instanceof ItemStack stack && !stack.isEmpty()) return stack.getItem();
        if (digger == null) return null;
        ItemStack held = digger.getMainHandItem();
        return held.isEmpty() ? null : held.getItem();
    }

    /**
     * Fills RELICS array with 100 item instances to return to the player
     */
    private static void buildTable(int bonus)
    {
        Arrays.fill(RELICS, null);
        int cursor = 0;

        for (Map.Entry<Item, Rareness> entry : RELIC_RARENESS.entrySet()) {
            int odds = getIntRareness(entry.getValue()) + bonus;
            if (odds <= 0) continue;

            for (int i = 0; i < odds; i++) {
                if (cursor < TABLE_SIZE) {
                    RELICS[cursor++] = entry.getKey();
                    continue;
                }

                int index = RANDOM.nextInt(OVERFLOW_RANGE);
                if (index < TABLE_SIZE) RELICS[index] = entry.getKey();
            }
        }
    }
        //get rareness value
        public static int getIntRareness(@Nullable Rareness rareness) {
            return rareness == null ? 0 : RARENESS_ODDS[rareness.ordinal()];
        }

}

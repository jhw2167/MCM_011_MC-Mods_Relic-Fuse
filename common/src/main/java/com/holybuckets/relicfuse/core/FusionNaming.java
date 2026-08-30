package com.holybuckets.relicfuse.core;

import com.holybuckets.relicfuse.Constants;
import com.holybuckets.relicfuse.item.fusable.BoneItem;
import com.holybuckets.relicfuse.item.fusable.CrystalItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Builds the display name and lore for a fused tool. Tier and tool type are parsed from the
 * tool's registry path, which only holds for vanilla-style naming; anything unrecognised falls
 * back to the tool's own display name.
 */
public class FusionNaming {

    public static final String TYPE_SWORD = "sword";
    public static final String TYPE_AXE = "axe";
    public static final String TYPE_PICK = "pick";
    public static final String TYPE_HOE = "hoe";
    public static final String TYPE_SHOVEL = "shovel";
    public static final String TYPE_TRIDENT = "trident";
    public static final String TYPE_SPEAR = "spear";
    public static final String TYPE_MACE = "mace";
    public static final String TYPE_UNKNOWN = "unknown";

    private static final Map<String, String> SUFFIX_TO_TYPE = Map.of(
        "_sword", TYPE_SWORD,
        "_axe", TYPE_AXE,
        "_pickaxe", TYPE_PICK,
        "_hoe", TYPE_HOE,
        "_shovel", TYPE_SHOVEL,
        "trident", TYPE_TRIDENT,
        "spear", TYPE_SPEAR,
        "mace", TYPE_MACE
    );

    // Fusion effects for these weapons ship without abilities.
    private static final Set<String> UNIMPLEMENTED_TYPES =
        Set.of(TYPE_TRIDENT, TYPE_SPEAR, TYPE_MACE);

    private static final String CRYSTAL_SUFFIX = " Crystal";

    /**
     * Crystals read qualifier-tier-tool, bones read tier-modifier-tool, anything else is a
     * plain concatenation of the two display names.
     */
    public static Component buildName(ItemStack modifier, ItemStack tool) {
        String toolType = toolType(tool);
        String tier = tier(tool);
        String modifierName = modifier.getItemName().getString();

        if (toolType.equals(TYPE_UNKNOWN) || tier == null) {
            return Component.literal(modifierName + " " + tool.getItemName().getString());
        }

        Component tierName = translatableOr("tier." + tier, capitalize(tier));
        Component typeName = translatableOr("tool." + toolType, capitalize(toolType));

        return switch (fusionKind(modifier)) {
            case CRYSTAL -> Component.empty()
                .append(stripSuffix(modifierName, CRYSTAL_SUFFIX))
                .append(" ").append(tierName)
                .append(" ").append(typeName);
            case BONE -> Component.empty()
                .append(tierName)
                .append(" ").append(modifierName)
                .append(" ").append(typeName);
            default -> Component.empty()
                .append(modifierName)
                .append(" ").append(tierName)
                .append(" ").append(typeName);
        };
    }

    /**
     * Sourced from en_us as item.hbs_relicfuse.fusion.&lt;modifier_path&gt;.&lt;tool_type&gt;
     */
    public static Component buildLore(ItemStack modifier, ItemStack tool) {
        String toolType = toolType(tool);
        if (UNIMPLEMENTED_TYPES.contains(toolType)) {
            return Component.translatable(
                "item." + Constants.MOD_ID + ".fusion.unimplemented." + toolType);
        }

        Identifier modifierId = BuiltInRegistries.ITEM.getKey(modifier.getItem());
        return Component.translatable(
            "item." + Constants.MOD_ID + ".fusion." + modifierId.getPath() + "." + toolType);
    }

    public static String toolType(ItemStack tool) {
        String path = BuiltInRegistries.ITEM.getKey(tool.getItem()).getPath();
        for (Map.Entry<String, String> entry : SUFFIX_TO_TYPE.entrySet()) {
            if (path.endsWith(entry.getKey())) return entry.getValue();
        }
        return TYPE_UNKNOWN;
    }

    private static String tier(ItemStack tool) {
        String path = BuiltInRegistries.ITEM.getKey(tool.getItem()).getPath();
        for (String suffix : SUFFIX_TO_TYPE.keySet()) {
            if (path.endsWith(suffix)) {
                String tier = path.substring(0, path.length() - suffix.length());
                return tier.isEmpty() ? null : tier;
            }
        }
        return null;
    }

    private static FusionKind fusionKind(ItemStack modifier) {
        if (modifier.getItem() instanceof CrystalItem) return FusionKind.CRYSTAL;
        if (modifier.getItem() instanceof BoneItem) return FusionKind.BONE;
        return FusionKind.OTHER;
    }

    private static Component translatableOr(String suffix, String fallback) {
        return Component.translatableWithFallback(
            "item." + Constants.MOD_ID + "." + suffix, fallback);
    }

    private static String stripSuffix(String value, String suffix) {
        return value.endsWith(suffix) ? value.substring(0, value.length() - suffix.length()) : value;
    }

    private static String capitalize(String value) {
        if (value.isEmpty()) return value;
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }

    private enum FusionKind {
        CRYSTAL,
        BONE,
        OTHER
    }

}

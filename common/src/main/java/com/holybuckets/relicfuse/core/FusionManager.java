package com.holybuckets.relicfuse.core;

import com.google.gson.JsonObject;
import com.holybuckets.foundation.console.IMessager;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.custom.PlayerInteractEvent;
import com.holybuckets.foundation.event.custom.SimpleMessageEvent;
import com.holybuckets.foundation.networking.SimpleStringMessage;
import com.holybuckets.relicfuse.CommonClass;
import com.holybuckets.relicfuse.Constants;
import com.holybuckets.relicfuse.component.FusionComponent;
import com.holybuckets.relicfuse.effect.ModEffects;
import com.holybuckets.relicfuse.item.IFusableItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class FusionManager {

    public static final String FUSE_START = "fuse_start";
    public static final String FUSE_COMPLETE = "fuse_complete";

    public static final String KEY_TOOL = "tool";
    public static final String KEY_FUSABLE = "fusable";
    public static final String KEY_RESULT = "result";

    private static final Identifier PLACEHOLDER_RESULT = Identifier.fromNamespaceAndPath("hbs_relicfuse", "iron_brush");

    private static final Map<ServerPlayer, Pair<ItemStack, ItemStack>> activeFusions = new HashMap<>();

    private static IMessager MESSAGER;

    public static void init(EventRegistrar reg) {
        reg.registerOnPlayerInteract(PlayerInteractEvent.RightClickInteraction.class, FusionManager::playerUseItem);
        reg.registerOnSimpleMessage(FUSE_COMPLETE, FusionManager::onFuseComplete);
        MESSAGER = CommonClass.MESSAGER;

        FusionAbilities.init(reg);
        FusionStats.init(reg);
        FusionItemWeights.init(reg);
    }



    //** CORE **//

    private static void playerUseItem(PlayerInteractEvent.RightClickInteraction event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (event.getLevel() == null || event.getLevel().isClientSide()) return;

        Player player = event.getPlayer();
        if (player == null || player.level().isClientSide()) return;
        if (!player.hasEffect(ModEffects.ancientPower)) return;

        ItemStack tool = player.getMainHandItem();
        ItemStack fusable = player.getOffhandItem();
        if (!isFusableTool(player, tool) || !isFusableItem(player, fusable)) return;

        String payload = buildPayload(tool, fusable);
        activeFusions.put((ServerPlayer) player, Pair.of(tool.copy(), fusable.copy()));

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);

        SimpleStringMessage.createAndFire(player, FUSE_START, payload);
    }

    private static void onFuseComplete(SimpleMessageEvent event)
    {
        if (event.getPlayer()==null || !(event.getPlayer() instanceof ServerPlayer player))
            return;
        Pair<ItemStack, ItemStack> stacks = activeFusions.remove(player);
        if (stacks == null) return;

        ItemStack tool = stacks.getLeft();
        ItemStack fusable = stacks.getRight();
        ItemStack result = fuse(fusable, tool);

        Vec3 pos = player.getEyePosition().add(player.getLookAngle().scale(1.0));
        ItemEntity drop = new ItemEntity(player.level(), pos.x, pos.y, pos.z, result);
        drop.setDeltaMovement(Vec3.ZERO);
        drop.setPickUpDelay(20);
        player.level().addFreshEntity(drop);
    }


    public static boolean isFusableTool(Player p, ItemStack tool) {
        if (tool.isEmpty()) {
            MESSAGER.sendBottomActionHint(p, readItemError("no_tool"));
            return false;
        } else if(tool.has(FusionComponent.TYPE) ) {
            MESSAGER.sendBottomActionHint(p, readItemError("fused_tool"));
            return false;
        } else if(!(tool.has(DataComponents.TOOL) || tool.getItem().equals(Items.BRUSH) )) {
            MESSAGER.sendBottomActionHint(p, readItemError("no_tool"));
            return false;
        }

        return true;
    }

    public static boolean isFusableItem(Player p, ItemStack modifier) {
        if (modifier.isEmpty()) {
            MESSAGER.sendBottomActionHint(p, readItemError("no_fusable"));
            return false;
        } else if(!(modifier.getItem() instanceof IFusableItem)) {
            MESSAGER.sendBottomActionHint(p, readItemError("unfusable_item"));
            return false;
        }

        return true;
    }

    /** Silent variants for validation away from a player interaction. */
    public static boolean isFusableTool(ItemStack tool) {
        return !tool.isEmpty() && !tool.has(FusionComponent.TYPE) && tool.has(DataComponents.TOOL);
    }

    public static boolean isFusableItem(ItemStack modifier) {
        return !modifier.isEmpty() && modifier.getItem() instanceof IFusableItem;
    }

    public static boolean isFused(ItemStack tool) {
        return tool != null && !tool.isEmpty() && FusionComponent.isFused(tool);
    }

    @Nullable
    public static Item getFusedItem(ItemStack tool) {
        if (!isFused(tool)) return null;
        return tool.get(FusionComponent.TYPE).getModifierOrNull();
    }


    private static String readItemError(String enUsField) {
        return READ("item", Constants.MOD_ID, "fusion.error." + enUsField);
    }

    public static String READ(String group, String modId, String enUsField) {
        return Component.translatable(group + "." + modId + "." + enUsField).getString();
    }


    /**
     * Result is hardcoded to the iron brush until fusion recipes exist.
     */
    public static Identifier resolveResult(ItemStack tool, ItemStack fusable) {
        return PLACEHOLDER_RESULT;
    }

    private static String buildPayload(ItemStack tool, ItemStack fusable) {
        JsonObject json = new JsonObject();
        json.addProperty(KEY_TOOL, BuiltInRegistries.ITEM.getKey(tool.getItem()).toString());
        json.addProperty(KEY_FUSABLE, BuiltInRegistries.ITEM.getKey(fusable.getItem()).toString());
        json.addProperty(KEY_RESULT, resolveResult(tool, fusable).toString());
        return json.toString();
    }

    /**
     * Takes tool and modifiying bone, crystal etc. Returns tool back after adding new fused component data
     * @param modifier
     * @param tool
     * @return tool
     */
    private static ItemStack fuse(ItemStack modifier, ItemStack tool)
    {
        if (modifier.isEmpty() || tool.isEmpty()) return ItemStack.EMPTY;
        if (!isFusableTool(tool) || !isFusableItem(modifier)) return ItemStack.EMPTY;

        ItemStack fused = tool.copy();
        FusionComponent.apply(fused, modifier.getItem());
        fused.set(DataComponents.ITEM_NAME, FusionNaming.buildName(modifier, tool));
        Component loreList = FusionNaming.buildLore(modifier, tool);
        fused.set(DataComponents.LORE, new ItemLore(List.of(loreList)));

        FusionStats.initFusedItem( fused, modifier);
        return fused;
    }


    private static boolean isFusedWith(ItemStack modifier, Item test) {
        if(modifier.isEmpty() || test == null) return false;
        if(!modifier.has(FusionComponent.TYPE)) return false;
        return modifier.equals(test);
    }

    private static List<Integer> tintsFor(Item modifier) {
        // one 5-colour ramp per modifier; gold placeholder until the table exists
        return List.of(7677954, 6835742, 11691025, 14456339, 16643423);
    }

}

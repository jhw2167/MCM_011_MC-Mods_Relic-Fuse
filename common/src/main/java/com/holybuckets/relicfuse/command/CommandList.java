package com.holybuckets.relicfuse.command;

import com.holybuckets.foundation.event.CommandRegistry;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.balm.server.ServerStartingEvent;
import com.holybuckets.relicfuse.core.FusionManager;
import com.holybuckets.relicfuse.item.IFusableItem;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CommandList {

    public static final String CLASS_ID = "034";
    private static final String PREFIX = "hbRelicFuse";

    public static int DEFAULT_RANDOM_COUNT = 1;
    public static int MAX_RANDOM_COUNT = 64;

    /** Every registered item the fuser will accept as a tool, and every fusable modifier. */
    private static final Set<Item> TOOLS = new LinkedHashSet<>();
    private static final Set<Item> FUSABLES = new LinkedHashSet<>();

    private static Random RANDOM;

    public static void init(EventRegistrar reg) {
        reg.registerOnBeforeServerStarted(CommandList::onBeforeServerStarted);
        register();
    }

    public static void register() {
        CommandRegistry.register(RandomFusion::noArgs);
        CommandRegistry.register(RandomFusion::withCount);
        CommandRegistry.register(Fuse::withToolAndFusable);
    }

    /**
     * The creative tab contents are only assembled for a client with feature flags resolved, so the
     * item registry is scanned instead. Anything carrying the TOOL component qualifies, plus the
     * brush and the spears, which FusionManager accepts by name.
     */
    private static void onBeforeServerStarted(ServerStartingEvent event) {
        RANDOM = new Random();
        TOOLS.clear();
        FUSABLES.clear();

        for (Item item : BuiltInRegistries.ITEM) {
            if (item.components().has(DataComponents.TOOL)
                || item.equals(Items.BRUSH)
                || item.getDescriptionId().contains("spear")) {
                TOOLS.add(item);
            }
            if (item instanceof IFusableItem) FUSABLES.add(item);
        }
    }

    private static final SuggestionProvider<CommandSourceStack> TOOL_SUGGESTIONS =
        (context, builder) -> SharedSuggestionProvider.suggest(TOOLS.stream().map(CommandList::id), builder);

    private static final SuggestionProvider<CommandSourceStack> FUSABLE_SUGGESTIONS =
        (context, builder) -> SharedSuggestionProvider.suggest(FUSABLES.stream().map(CommandList::id), builder);

    private static String id(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }

    @Nullable
    private static Item lookup(String name) {
        Identifier loc = Identifier.tryParse(name.contains(":") ? name : "minecraft:" + name);
        if (loc == null) return null;
        return BuiltInRegistries.ITEM.getOptional(loc).orElse(null);
    }

    private static int give(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty()) return 0;
        if (!player.getInventory().add(stack)) player.drop(stack, false);
        return 1;
    }


    //**** RANDOM FUSION ****//

    private static class RandomFusion {

        private static LiteralArgumentBuilder<CommandSourceStack> noArgs() {
            return Commands.literal(PREFIX)
                .then(Commands.literal("randomFusion")
                    .executes(context -> execute(context.getSource(), DEFAULT_RANDOM_COUNT)));
        }

        private static LiteralArgumentBuilder<CommandSourceStack> withCount() {
            return Commands.literal(PREFIX)
                .then(Commands.literal("randomFusion")
                    .then(Commands.argument("count", IntegerArgumentType.integer(1, MAX_RANDOM_COUNT))
                        .executes(context -> execute(context.getSource(),
                            IntegerArgumentType.getInteger(context, "count")))));
        }

        private static int execute(CommandSourceStack source, int count) {
            if (!(source.getEntity() instanceof ServerPlayer player)) {
                source.sendFailure(Component.literal("This command can only be used by players"));
                return 0;
            }

            if (TOOLS.isEmpty() || FUSABLES.isEmpty()) {
                source.sendFailure(Component.literal("No tools or fusables registered yet"));
                return 0;
            }

            List<Item> tools = new ArrayList<>(TOOLS);
            List<Item> fusables = new ArrayList<>(FUSABLES);
            int made = 0;

            for (int i = 0; i < count; i++) {
                ItemStack tool = new ItemStack(tools.get(RANDOM.nextInt(tools.size())));
                ItemStack fusable = new ItemStack(fusables.get(RANDOM.nextInt(fusables.size())));
                made += give(player, FusionManager.fuseDirect(tool, fusable));
            }

            final int total = made;
            source.sendSuccess(() -> Component.literal("Fused " + total + " random relics"), false);
            return total;
        }
    }


    //**** EXPLICIT FUSION ****//

    private static class Fuse {

        private static LiteralArgumentBuilder<CommandSourceStack> withToolAndFusable() {
            return Commands.literal(PREFIX)
                .then(Commands.literal("fuse")
                    .then(Commands.argument("tool", StringArgumentType.string())
                        .suggests(TOOL_SUGGESTIONS)
                        .then(Commands.argument("fusable", StringArgumentType.string())
                            .suggests(FUSABLE_SUGGESTIONS)
                            .executes(context -> execute(context.getSource(),
                                StringArgumentType.getString(context, "tool"),
                                StringArgumentType.getString(context, "fusable"))))));
        }

        private static int execute(CommandSourceStack source, String toolName, String fusableName) {
            if (!(source.getEntity() instanceof ServerPlayer player)) {
                source.sendFailure(Component.literal("This command can only be used by players"));
                return 0;
            }

            Item tool = lookup(toolName);
            if (tool == null) {
                source.sendFailure(Component.literal("Unknown item: " + toolName));
                return 0;
            }

            Item fusable = lookup(fusableName);
            if (fusable == null) {
                source.sendFailure(Component.literal("Unknown item: " + fusableName));
                return 0;
            }

            ItemStack result = FusionManager.fuseDirect(new ItemStack(tool), new ItemStack(fusable));
            if (result.isEmpty()) {
                source.sendFailure(Component.literal(
                    toolName + " cannot be fused with " + fusableName));
                return 0;
            }

            give(player, result);
            source.sendSuccess(() -> Component.literal("Fused " + result.getHoverName().getString()), false);
            return 1;
        }
    }
}

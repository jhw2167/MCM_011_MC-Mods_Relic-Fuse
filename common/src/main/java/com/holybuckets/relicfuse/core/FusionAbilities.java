package com.holybuckets.relicfuse.core;

import com.google.gson.JsonObject;
import com.holybuckets.foundation.GeneralConfig;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.balm.server.ServerStartingEvent;
import com.holybuckets.foundation.networking.SimpleStringMessage;
import com.holybuckets.relicfuse.effect.ToxicEffect;
import com.holybuckets.relicfuse.item.tool.FusedShovelItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;

import com.holybuckets.foundation.AAA.ShapeGen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import com.holybuckets.relicfuse.item.tool.FusedAxeItem;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.relicfuse.item.ModItems;
import com.holybuckets.relicfuse.item.tool.FusedHoeItem;
import com.holybuckets.relicfuse.item.tool.FusedPickaxeItem;
import com.holybuckets.relicfuse.item.tool.FusedSwordItem;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.VineBlock;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * One class per fusable modifier. ManagedFusions routes every player event here based on the
 * FusionComponent on the held tool, so each hook only fires while that modifier is fused.
 *
 * Hooks:
 * - swordOnHurt: player damages target
 * - swordOnDeath: target dies to the player
 * - onSwing: left click, block or air
 * - toolOnRightClick: right click, block or air
 * - toolOnEntityInteract: right click on an entity
 * - toolOnUseBlock: right click on a block
 * - toolOnMineBlock: each tick while mining
 * - toolOnBreakBlock: block is broken
 * - onPlayerHurt: player takes damage
 * - onPlayerFall: player lands
 * - onPlayerHeal: player heals
 * - onPlayerDeath: player dies
 * - onTossItem: player drops an item
 * - onChangedDimension: player changes dimension
 * - onRespawn: player respawns
 * - onLogin: player joins
 * - onLogout: player leaves
 */
public class FusionAbilities {

    private static Registry<DamageType> DAMAGE_TYPES_REGISTRY;
    private static ReloadableServerRegistries.Holder LOOT_TABLES_REGISTRY;
    private static MinecraftServer SERVER;

    private static GeneralConfig CONFIG;
    private static Random RANDOM;

    public static void init(EventRegistrar registrar) {
        registrar.registerOnBeforeServerStarted(FusionAbilities::onBeforeServerStarted);
    }

    public static void onBeforeServerStarted(ServerStartingEvent event) {

        CONFIG = GeneralConfig.getInstance();
        RANDOM = new Random(CONFIG.getWorldSeed()*(CONFIG.getTotalTickCount()+1) );

        SERVER = event.getServer();
        DAMAGE_TYPES_REGISTRY = SERVER.registryAccess()
            .lookupOrThrow(Registries.DAMAGE_TYPE);

        LOOT_TABLES_REGISTRY = SERVER.reloadableRegistries();


        DemonicCrystal.init();
        ElectricCrystal.init();
    }

    private static boolean tryChance(float chance) {
        return RANDOM.nextFloat() < chance;
    }

    //Use shapegen to return a random list of blocks in the area
    private static List<BlockPos> getRandomBlocksInArea(BlockPos center, int width, int height, int depth, float ignoreChance) {
        List<BlockPos> blocks = ShapeGen.cuboid(center, width, height, depth);
        Collections.shuffle(blocks, RANDOM);
        List<BlockPos> result = new ArrayList<>();
        for (BlockPos pos : blocks) {
            if (tryChance(ignoreChance)) continue;
            result.add(pos);
        }
        return result;
    }

    /** True when the attack cooldown has fully recovered, matching the vanilla crit window. */
    private static boolean isFullyCharged(ServerPlayer player) {
        return player.getAttackStrengthScale(0.5F) >= FULL_CHARGE;
    }

    private static final float FULL_CHARGE = 0.9F;

    private static boolean isAxe(ItemStack tool) {
        return tool.getItem() instanceof AxeItem || tool.getItem() instanceof FusedAxeItem;
    }

    private static boolean isHoe(ItemStack tool) {
        return tool.getItem() instanceof HoeItem || tool.getItem() instanceof FusedHoeItem;
    }

    private static boolean isShovel(ItemStack tool) {
        return tool.getItem() instanceof ShovelItem || tool.getItem() instanceof FusedShovelItem;
    }

    private static boolean isPickaxe(ItemStack tool) {
        return tool.getItem() instanceof FusedPickaxeItem
            || tool.getItemName().getString().toLowerCase().contains("pickaxe");
    }

    private static boolean isSword(ItemStack tool) {
        return tool.getItem() instanceof FusedSwordItem
            || tool.getItemName().getString().toLowerCase().contains("sword");
    }

    private static List<BlockPos> centeredArea(BlockPos center, int radius, int height) {
        return ShapeGen.cuboid(center.offset(-radius, -height, -radius),
            radius * 2 + 1, height * 2 + 1, radius * 2 + 1);
    }



    public static class BlessedCrystal {

        private static final int xpBonusDrop = 5; //bonus xp to drop on kill

        public static void swordOnDeath(ServerPlayer player, ItemStack tool, DamageSource source, LivingEntity target) {
            int xp = target.getExperienceReward(player.level(), player);
            player.level().addFreshEntity(new ExperienceOrb(player.level(), target.getX(), target.getY(), target.getZ(), xp));
        }

        public static void toolOnBreakBlock(ServerPlayer player, ItemStack tool, BlockPos pos, BlockState state) {
            //test if the block name contains the word ore or if it drops experience, if so, drop extra experience
            String blockName = state.getBlock().getName().getString().toLowerCase();
            if(isAxe(tool)) {
                if(!blockName.contains(" log")) return;
            }
            if(isPickaxe(tool)) {
                if(blockName.contains(" ore")) {}//good
                else if(state.getBlock() instanceof DropExperienceBlock)
                {} else { return; }
            }

            player.level().addFreshEntity(new ExperienceOrb(player.level(), pos.getX(), pos.getY(), pos.getZ(), xpBonusDrop));
        }

    }

    public static class DemonicCrystal {

        private static DamageSource MAGIC_SOURCE;

        private static void init() {
            MAGIC_SOURCE = new DamageSource(Holder.direct(DAMAGE_TYPES_REGISTRY.getValue(DamageTypes.MAGIC)));
        }

        private static final float DEMONIC_DAMAGE = 2.0F;
        //do half a heart of damage to the player when they hurt anything
        public static void swordOnDeath(ServerPlayer player, ItemStack tool, DamageSource source, Entity target) {
            player.hurtServer(player.level(), MAGIC_SOURCE, DEMONIC_DAMAGE);
        }

        public static void toolOnBreakBlock(ServerPlayer player, ItemStack tool, BlockPos pos, BlockState state) {
            player.hurtServer(player.level(), MAGIC_SOURCE, DEMONIC_DAMAGE / 2);
        }

    }

    public static class EarthCrystal {

        private static float EARTH_LOOTING_CHANCE = 0.25F;

        public static void swordOnDeath(ServerPlayer player, ItemStack tool, DamageSource source, LivingEntity target) {
            if(target.getLootTable().isEmpty()) return;
            if(!tryChance(EARTH_LOOTING_CHANCE)) return;
            target.dropFromLootTable(player.level(), source, true, target.getLootTable().get());
        }

        public static void toolOnBreakBlock(ServerPlayer player, ItemStack tool, BlockPos pos, BlockState state) {
            if(state.getBlock().getLootTable().isEmpty()) return;
            if(!tryChance(EARTH_LOOTING_CHANCE)) return;

            //check if tool is appropriate for the block, if not, don't drop loot
            if(state.getDestroySpeed(player.level(), pos) <= 0) return;
            LootTable table = LOOT_TABLES_REGISTRY.getLootTable( state.getBlock().getLootTable().get() );
            state.getDrops(new LootParams.Builder(player.level()).withLuck(player.getLuck())
            ).forEach(stack -> {
                player.level().addFreshEntity(new ItemEntity(player.level(), pos.getX(), pos.getY(), pos.getZ(), stack));
            });
        }

    }

    /**
     * Fully charged strikes arc lightning to nearby mobs. Applicable tools vein-mine matching
     * blocks; the tool must be the right one for the job, so pickaxes chain ore and axes chain logs.
     */
    public static class ElectricCrystal {

        private static final float CHARGE_THRESHOLD = 0.9F;
        private static final double CHAIN_RADIUS = 10.0;
        private static final int MAX_CHAIN_TARGETS = 8;
        private static final float CHAIN_DAMAGE = 4.0F;
        private static final int MAX_VEIN_BLOCKS = 8;

        private static  Map<Block, List<Block>> shovelTillableblocks;

        private static final List<Block> STONE_RESULTS = List.of(
            Blocks.BASALT, Blocks.TUFF, Blocks.DIORITE, Blocks.ANDESITE, Blocks.GRANITE);

        private static final Queue<Entity> CHAINED_MOBS = new ArrayDeque<>();
        private static final Map<Level, Queue<BlockPos>> CHAINED_BLOCKS = new HashMap<>();

        private static void init() {
            shovelTillableblocks = new HashMap<>();
            shovelTillableblocks.put(Blocks.GRAVEL, List.of(Blocks.BASALT));
            shovelTillableblocks.put(Blocks.SAND, List.of(Blocks.GLASS));
            shovelTillableblocks.put(Blocks.RED_SAND, List.of(
                Blocks.RED_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS));

            //clay to brick
            shovelTillableblocks.put(Blocks.CLAY, List.of(Blocks.BRICKS));
            shovelTillableblocks.put(Blocks.DIRT, STONE_RESULTS);
            shovelTillableblocks.put(Blocks.GRASS_BLOCK, STONE_RESULTS);
            shovelTillableblocks.put(Blocks.COARSE_DIRT, List.of(Blocks.GRAVEL));

            shovelTillableblocks.put(Blocks.MUD, List.of(Blocks.COBBLESTONE));
            shovelTillableblocks.put(Blocks.PACKED_MUD, List.of(Blocks.MUD_BRICKS));
            shovelTillableblocks.put(Blocks.SNOW_BLOCK, List.of(Blocks.ICE));
            shovelTillableblocks.put(Blocks.SOUL_SAND, List.of(Blocks.SOUL_SOIL));
            shovelTillableblocks.put(Blocks.SOUL_SOIL, List.of(Blocks.BASALT));
        }

        @Nullable
        private static Block tilledResult(Block source) {
            List<Block> results = shovelTillableblocks.get(source);
            if (results == null || results.isEmpty()) return null;
            int idx = RANDOM.nextInt(results.size());
            return results.get(idx);
        }

        private static final float IGNORE_CHANCE = 0.20F;
        public static void toolOnUseBlock(ServerPlayer player, ItemStack tool, BlockPos pos)
        {
            if(!isTillable(tool, player.level().getBlockState(pos))) return;

            List<BlockPos> blocks = getRandomBlocksInArea(pos, 1, 1, 1, IGNORE_CHANCE);
            if(!blocks.contains(pos)) blocks.add(pos);
            blocks.forEach(blockPos -> {
                BlockState state = player.level().getBlockState(blockPos);
                if (isTillable(tool, state)) {
                    Block newBlock = tilledResult(state.getBlock());
                    if (newBlock != null) {
                    //play ligning effect and sound
                        strike(player.level(), player, blockPos);
                        player.level().setBlockAndUpdate(blockPos, newBlock.defaultBlockState());
                    }
                }
            });
        }

        //When fully charged, lightnight strikes other players in the area
        public static void swordOnHurt(ServerPlayer player, ItemStack tool, Entity target) {
            if (!isFullyCharged(player)) return;
            if (!(player.level() instanceof ServerLevel level)) return;

            AABB area = target.getBoundingBox().inflate(CHAIN_RADIUS);
            List<Mob> chained = level.getEntitiesOfClass(Mob.class, area, mob -> mob != target && mob.isAlive());
            chained.sort(Comparator.comparingDouble(mob -> mob.distanceToSqr(target)));

            CHAINED_MOBS.addAll( chained.stream().limit(MAX_CHAIN_TARGETS).collect(Collectors.toSet()) );
        }



        public static void toolOnBreakBlock(ServerPlayer player, ItemStack tool, BlockPos pos, BlockState state)
        {
            ServerLevel level = player.level();
            if (!isVeinable(tool, state)) return;

            Block target = state.getBlock();
            Set<BlockPos> mined = new HashSet<>();
            Deque<BlockPos> frontier = new ArrayDeque<>();
            mined.add(pos);
            frontier.add(pos);

            while (!frontier.isEmpty() && mined.size() < MAX_VEIN_BLOCKS) {
                BlockPos current = frontier.poll();
                for (BlockPos neighbour : ShapeGen.cuboid(current.offset(-1, -1, -1), 3, 3, 3)) {
                    if (mined.size() >= MAX_VEIN_BLOCKS) break;
                    if (mined.contains(neighbour)) continue;
                    if (!level.getBlockState(neighbour).is(target)) continue;

                    mined.add(neighbour);
                    frontier.add(neighbour.immutable());
                }
            }
            CHAINED_BLOCKS.computeIfAbsent(level, k -> new ArrayDeque<>()).addAll(mined);
        }


        public static final String STRIKE_MSG_ID = "weapon_attack_strike";
        private static void strike(ServerLevel level, Entity from, Entity to) {
            JsonObject msg = new JsonObject();
            msg.addProperty("from", from.getId());
            msg.addProperty("to", to.getId());
            msg.addProperty("level", HBUtil.LevelUtil.toLevelIdAgnostic(level));

            SimpleStringMessage.createAndFire(STRIKE_MSG_ID, msg.toString());
        }

        //overload the above for blockPosition
        public static final String BREAK_MSG_ID = "weapon_attack_break";
        private static void strike(ServerLevel level, BlockPos from, BlockPos to) {
            JsonObject msg = new JsonObject();
            msg.addProperty("from", HBUtil.BlockUtil.positionToString(from) );
            msg.addProperty("to", HBUtil.BlockUtil.positionToString(to) );
            msg.addProperty("level", HBUtil.LevelUtil.toLevelIdAgnostic(level));

            SimpleStringMessage.createAndFire(BREAK_MSG_ID, msg.toString());
        }

        /**
         * Visual only; damage is applied directly so the bolt cannot start fires or convert mobs.
         */
        //classic entity based strike
        private static void strike(ServerLevel level, Entity from, BlockPos to) {
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
            if (bolt == null) return;

            bolt.setPos(to.getX() + 0.5, to.getY(), to.getZ() + 0.5);
            bolt.setVisualOnly(true);
            level.addFreshEntity(bolt);
        }

        private static final int LIGHTNING_TICK_INTERVAL = 10;
        private static final Queue<BlockPos> EMPTY_QUEUE = new ArrayDeque<>();
        private static int count = 0;
        public static void onTick(ServerLevel level)
        {
            if(count++<LIGHTNING_TICK_INTERVAL) return;
            count = 0;
            //iterate chained mobs and strike
            if( !CHAINED_MOBS.isEmpty())
            {
                Entity popped = CHAINED_MOBS.poll();
                Entity peeked = CHAINED_MOBS.peek();
                if(popped==null || peeked==null) return;

                //if distance exceeds CHAIN_RADIUS, return
                strike(level,  popped, peeked);
                peeked.hurtServer(level, level.damageSources().lightningBolt(), CHAIN_DAMAGE);

            }

            if( CHAINED_BLOCKS.getOrDefault(level, EMPTY_QUEUE).isEmpty() ) return;
            {
                BlockPos popped = CHAINED_BLOCKS.get(level).poll();
                BlockPos peeked = CHAINED_BLOCKS.get(level).peek();
                if(popped==null || peeked==null) return;

                //if distance exceeds CHAIN_RADIUS, return
                if(popped.distSqr(peeked) > CHAIN_RADIUS * CHAIN_RADIUS) return;

                strike(level,  popped, peeked);
                level.destroyBlock(peeked, true, null, 512);
            }
        }


        /**
         * Only the tool that would normally harvest the block chains it. Hoes and shovels get their
         * own treatment later, so they are excluded here.
         */
        private static boolean isVeinable(ItemStack tool, BlockState state) {
            String block = state.getBlock().getName().getString().toLowerCase();
            Item item = tool.getItem();
            if (item.components().has(DataComponents.TOOL) && tool.getItemName().getString().toLowerCase().contains("pickaxe")) {
                return block.contains("ore") && item.isCorrectToolForDrops(tool, state);
            }
            if (item instanceof AxeItem || item instanceof FusedAxeItem) return block.contains("log");
            return false;
        }

        //right clicking with a shovel
        private static boolean isTillable(ItemStack tool, BlockState state) {
            Item item = tool.getItem();
            if (item instanceof ShovelItem || item instanceof FusedShovelItem)
                return shovelTillableblocks.containsKey(state.getBlock());
            return false;
        }

    }

    /**
     * Weapons stack poison the harder the swing lands. Tools dissolve a cuboid of blocks over the
     * following seconds, with only a small chance of any given block yielding its loot.
     */
    public static class ToxicCrystal {

        private static final float CHARGE_THRESHOLD = 0.9F;
        private static final int POISON_DURATION = 100;
        private static final int MAX_POISON_AMPLIFIER = 3;

        private static final int AREA_WIDTH = 2;
        private static final int AREA_HEIGHT = 1;
        private static final int AREA_DEPTH = 2;
        private static final int BELOW = 0;
        private static final float LOOT_CHANCE = 0.25F;
        private static final float SKIP_CHANCE = 0.15F;
        private static final int MAX_DISSOLVE_SECOND = 10;

        private static final Map<ServerLevel, Set<BlockPos>> dissolvedMap = new HashMap<>();

        public static void swordOnHurt(ServerPlayer player, ItemStack tool, Entity target) {
            if (!(target instanceof LivingEntity living)) return;
            if (!isFullyCharged(player)) return;

            MobEffectInstance existing = living.getEffect(MobEffects.POISON);
            int amplifier = (existing==null ? 0 : Math.min(existing.getAmplifier() + 1, MAX_POISON_AMPLIFIER));
            living.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION, amplifier), player);
        }

        //Toxicity seeps into surrounding blocks and breaks them
        public static void toolOnBreakBlock(ServerPlayer player, ItemStack tool, BlockPos pos, BlockState state)
        {
            ServerLevel level = player.level();
            if(!tool.isCorrectToolForDrops(state)) return;

            List<BlockPos> contaminated = getRandomBlocksInArea(pos, AREA_WIDTH, AREA_HEIGHT, AREA_DEPTH, 1-SKIP_CHANCE);
            for(int i = 0; i < contaminated.size(); i++)
            {
                BlockPos afflicted = contaminated.get(i);
                if (level.getBlockState(afflicted).isAir()) continue;
                dissolvedMap.computeIfAbsent(player.level(), k -> new HashSet<>()).add(afflicted.immutable());
                if( tryChance(SKIP_CHANCE) ) continue;
                contaminated.add(afflicted.below());
            }
        }

        /**
         * Emits toxic particles from every queued block each tick, then dissolves the whole queue
         * once per second.
         */
        public static void onTick(ServerLevel level)
        {
            if (dissolvedMap.isEmpty() || dissolvedMap.get(level)==null ) return;

            for (BlockPos pos : dissolvedMap.get(level)) {
                ToxicEffect.emit(level, pos);
            }
        }

        public static void dissolveQueued20Ticks(ServerLevel level)
        {
            if( dissolvedMap.isEmpty() || dissolvedMap.get(level)==null ) return;
            final int maxDissolve = Math.max(MAX_DISSOLVE_SECOND, dissolvedMap.get(level).size() / 10); //watch runaway
            List<BlockPos> dissolveList = dissolvedMap.get(level).stream().toList();
            for(int i = 0; i < dissolveList.size(); i++)
            {
                if(i >= maxDissolve) break;
                BlockPos pos = dissolveList.get(i);
                dissolvedMap.get(level).remove(pos);
                if (level.getBlockState(pos).isAir()) continue;
                if (tryChance(SKIP_CHANCE)) continue;
                /*if (tryChance(LOOT_CHANCE)) {
                    level.getBlockState(pos).getDrops(new LootParams.Builder(level))
                        .forEach(stack -> level.addFreshEntity(
                            new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack)));
                }*/
                level.destroyBlock(pos, tryChance(LOOT_CHANCE));
            }
        }

    }

    public static class FireCrystal {

        public static void swordOnHurt(ServerPlayer player, ItemStack tool, Entity target) {
        }

        public static void swordOnDeath(ServerPlayer player, ItemStack tool, LivingEntity target) {
        }

        public static void onSwing(ServerPlayer player, ItemStack tool) {
        }

        public static void toolOnRightClick(ServerPlayer player, ItemStack tool) {
        }

        public static void toolOnEntityInteract(ServerPlayer player, ItemStack tool, Entity target) {
        }

        public static void toolOnUseBlock(ServerPlayer player, ItemStack tool, BlockPos pos) {
        }

        public static void toolOnMineBlock(ServerPlayer player, ItemStack tool, BlockPos pos) {
        }

        public static void toolOnBreakBlock(ServerPlayer player, ItemStack tool, BlockPos pos, BlockState state) {
        }

        public static void onPlayerHurt(ServerPlayer player, ItemStack tool, DamageSource source, float amount) {
        }

        public static void onPlayerFall(ServerPlayer player, ItemStack tool, float fallDamage) {
        }

        public static void onPlayerHeal(ServerPlayer player, ItemStack tool, float amount) {
        }

        public static void onPlayerDeath(ServerPlayer player, ItemStack tool, DamageSource source) {
        }

        public static void onTossItem(ServerPlayer player, ItemStack tool, ItemStack tossed) {
        }

        public static void onChangedDimension(ServerPlayer player, ItemStack tool) {
        }

        public static void onRespawn(ServerPlayer player, ItemStack tool) {
        }

        public static void onLogin(ServerPlayer player, ItemStack tool) {
        }

        public static void onLogout(ServerPlayer player, ItemStack tool) {
        }

    }

    /* BONES */

    //Just adds 2 levels of unbreaking to tool
    public static class EncasedBone {

    }

    // Weapons slow enemies with vine like effect
    // Hoe effectively gives bone meal bonuses to tilled dirt
    // Shovel - bonus drops from dirt - like random seeds
    // Pick - Right Clicking adds vines to stone and ores
    // axe - Right Clicking adds vines to logs and planks
    // Trident, Mace
    public static class OvergrownBone {

        private static final int SLOW_DURATION = 60;
        private static final int MAX_SLOW_AMPLIFIER = 2;

        private static final int VINE_RADIUS = 2;
        private static final int VINE_HEIGHT = 1;
        private static final float VINE_SKIP_CHANCE = 0.65F;
        private static final int MAX_VINES = 6;

        private static final int GROW_RADIUS = 1;
        private static final float SEED_CHANCE = 0.20F;

        private static final List<Item> SEEDS = List.of(
            Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.MELON_SEEDS,
            Items.PUMPKIN_SEEDS, Items.TORCHFLOWER_SEEDS);

        public static void swordOnHurt(ServerPlayer player, ItemStack tool, Entity target) {
            if (!(target instanceof LivingEntity living)) return;

            MobEffectInstance existing = living.getEffect(MobEffects.SLOWNESS);
            int amplifier = existing == null ? 0 : Math.min(existing.getAmplifier() + 1, MAX_SLOW_AMPLIFIER);
            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, SLOW_DURATION, amplifier), player);

            player.level().sendParticles(ParticleTypes.COMPOSTER,
                living.getX(), living.getY() + 0.5, living.getZ(), 6, 0.3, 0.4, 0.3, 0.0);
        }

        public static void toolOnUseBlock(ServerPlayer player, ItemStack tool, BlockPos pos) {
            ServerLevel level = player.level();
            if (isHoe(tool)) {
                fertilize(level, pos);
            } else if (isPickaxe(tool)) {
                creep(level, pos, OvergrownBone::isStoneLike);
            } else if (isAxe(tool)) {
                creep(level, pos, OvergrownBone::isWoodLike);
            }
        }

        public static void toolOnBreakBlock(ServerPlayer player, ItemStack tool, BlockPos pos, BlockState state) {
            if (!isShovel(tool)) return;
            if (!state.is(BlockTags.DIRT)) return;
            if (!tryChance(SEED_CHANCE)) return;

            Item seed = SEEDS.get(RANDOM.nextInt(SEEDS.size()));
            player.level().addFreshEntity(new ItemEntity(player.level(),
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(seed)));
        }

        private static void fertilize(ServerLevel level, BlockPos pos) {
            for (BlockPos target : centeredArea(pos, GROW_RADIUS, 0)) {
                BlockPos immutable = target.immutable();
                if (BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), level, immutable)) {
                    BoneMealItem.addGrowthParticles(level, immutable, 4);
                }
            }
        }

        private static void creep(ServerLevel level, BlockPos origin, Predicate<BlockState> anchor) {
            List<BlockPos> candidates = centeredArea(origin, VINE_RADIUS, VINE_HEIGHT);
            Collections.shuffle(candidates, RANDOM);

            int placed = 0;
            for (BlockPos pos : candidates) {
                if (placed >= MAX_VINES) break;
                if (tryChance(VINE_SKIP_CHANCE)) continue;
                if (!level.getBlockState(pos).isAir()) continue;

                BlockState vine = vineAgainst(level, pos, anchor);
                if (vine == null) continue;

                level.setBlockAndUpdate(pos.immutable(), vine);
                placed++;
            }
        }

        private static BlockState vineAgainst(ServerLevel level, BlockPos pos, Predicate<BlockState> anchor) {
            BlockState vine = Blocks.VINE.defaultBlockState();
            boolean attached = false;

            for (Direction dir : Direction.Plane.HORIZONTAL) {
                if (!anchor.test(level.getBlockState(pos.relative(dir)))) continue;
                vine = vine.setValue(VineBlock.PROPERTY_BY_DIRECTION.get(dir), true);
                attached = true;
            }

            if (!attached && anchor.test(level.getBlockState(pos.above()))) {
                vine = vine.setValue(VineBlock.UP, true);
                attached = true;
            }

            return attached ? vine : null;
        }

        private static boolean isStoneLike(BlockState state) {
            return state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.getBlock().getName().getString().toLowerCase().contains("ore");
        }

        private static boolean isWoodLike(BlockState state) {
            return state.is(BlockTags.LOGS) || state.is(BlockTags.PLANKS);
        }

    }


    //Grants player random beneficial bonus potion effects while using their tool:
    // Swords - Strength, Speed, Regeneration
    // Axes - Haste, Resistance, Luck
    // Pickaxes - Haste, Night Vision, Water Breathing, Regen
    // Shovels - Haste, Jump Boost, Slow Falling
    // Hoes - Haste, Luck, Regeneration
    public static class SpiritedBone {

        private static final float EFFECT_CHANCE = 0.1F;
        private static final int BOON_DURATION = 200;

        private static final List<Holder<MobEffect>> SWORD_BOONS =
            List.of(MobEffects.STRENGTH, MobEffects.SPEED, MobEffects.REGENERATION);
        private static final List<Holder<MobEffect>> AXE_BOONS =
            List.of(MobEffects.HASTE, MobEffects.RESISTANCE, MobEffects.LUCK);
        private static final List<Holder<MobEffect>> PICKAXE_BOONS =
            List.of(MobEffects.HASTE, MobEffects.NIGHT_VISION, MobEffects.WATER_BREATHING, MobEffects.REGENERATION);
        private static final List<Holder<MobEffect>> SHOVEL_BOONS =
            List.of(MobEffects.HASTE, MobEffects.JUMP_BOOST, MobEffects.SLOW_FALLING);
        private static final List<Holder<MobEffect>> HOE_BOONS =
            List.of(MobEffects.HASTE, MobEffects.LUCK, MobEffects.REGENERATION);

        public static void onSwing(ServerPlayer player, ItemStack tool) {
            grantEffect(player, tool);
        }

        public static void toolOnBreakBlock(ServerPlayer player, ItemStack tool, BlockPos pos, BlockState state) {
            grantEffect(player, tool);
        }

        private static void grantEffect(ServerPlayer player, ItemStack tool) {
            if(player.getActiveEffects().isEmpty())
                if(!tryChance(3 * EFFECT_CHANCE)) return;
            else
                if(!tryChance(EFFECT_CHANCE)) return;

            List<Holder<MobEffect>> pool = poolFor(tool);
            if (pool.isEmpty()) return;

            Holder<MobEffect> boon = pool.get(RANDOM.nextInt(pool.size()));
            if (player.hasEffect(boon)) return;

            player.addEffect(new MobEffectInstance(boon, BOON_DURATION, 0));
            player.level().sendParticles(ParticleTypes.HAPPY_VILLAGER,
                player.getX(), player.getY() + 1.0, player.getZ(), 8, 0.4, 0.6, 0.4, 0.0);
        }

        private static List<Holder<MobEffect>> poolFor(ItemStack tool) {
            if (isSword(tool)) return SWORD_BOONS;
            if (isAxe(tool)) return AXE_BOONS;
            if (isPickaxe(tool)) return PICKAXE_BOONS;
            if (isShovel(tool)) return SHOVEL_BOONS;
            if (isHoe(tool)) return HOE_BOONS;
            return List.of();
        }

    }

    //user gets
    public static class ToxicBone {

        public static void swordOnHurt(ServerPlayer player, ItemStack tool, Entity target) {
            if(target instanceof LivingEntity living) {
                //doesnt look like there is poison resistance effect
                DamageSource poison = new DamageSource(Holder.direct(DAMAGE_TYPES_REGISTRY.getValue(DamageTypes.MAGIC)));
                living.hurtServer(player.level(), poison, 1.0F);
            }
        }

        //gives user poison effect for 5 seconds
        public static void onSwing(ServerPlayer player, ItemStack tool) {
            MobEffectInstance poison = new MobEffectInstance(MobEffects.POISON, 100, 0);
            player.addEffect(poison);
        }

        //If the destroy progress is above 80%, break the block instantly
        public static void toolOnMineBlock(ServerPlayer player, ItemStack tool, BlockPos pos) {
            //mine blocks faster
            BlockState state = player.level().getBlockState(pos);
            if(state.getDestroyProgress(player, player.level(), pos) > 0.8F) {
                player.level().destroyBlock(pos, true, player);
            }
        }

    }

    //Adds soulbound to all weapons
    public static class EnderBone {

        private static final Map<String, List<ItemStack>> SOULBOUND = new HashMap<>();

        /**
         * Pulled out of the inventory before vanilla drops it, so the stacks are never on the ground
         * and cannot be duplicated regardless of the keep-inventory rule.
         */
        public static void stashOnDeath(ServerPlayer player) {
            Item enderBone = ModItems.enderBone == null ? null : ModItems.enderBone.get();
            if (enderBone == null) return;

            Inventory inventory = player.getInventory();
            List<ItemStack> kept = new ArrayList<>();

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (stack.isEmpty()) continue;
                if (!enderBone.equals(FusionManager.getFusedItem(stack))) continue;

                kept.add(stack.copy());
                inventory.setItem(i, ItemStack.EMPTY);
            }

            if (kept.isEmpty()) return;
            SOULBOUND.put(HBUtil.PlayerUtil.getId(player), kept);
        }

        public static void restoreOnRespawn(ServerPlayer player) {
            List<ItemStack> kept = SOULBOUND.remove(HBUtil.PlayerUtil.getId(player));
            if (kept == null) return;
            kept.forEach(stack -> player.getInventory().placeItemBackInInventory(stack));
        }

    }

}

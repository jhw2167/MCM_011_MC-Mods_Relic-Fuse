package com.holybuckets.relicfuse.core;

import com.holybuckets.foundation.GeneralConfig;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.balm.server.ServerStartingEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
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

import java.util.*;

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
    private static Registry<LootTable> LOOT_TABLES_REGISTRY;
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

        LOOT_TABLES_REGISTRY = SERVER.registryAccess()
            .lookupOrThrow(Registries.LOOT_TABLE);


        DemonicCrystal.init();
    }

    private static boolean tryChance(float chance) {
        return RANDOM.nextFloat() < chance;
    }

    /** True when the attack cooldown has fully recovered, matching the vanilla crit window. */
    private static boolean isFullyCharged(ServerPlayer player) {
        return player.getAttackStrengthScale(0.5F) >= FULL_CHARGE;
    }

    private static final float FULL_CHARGE = 0.9F;



    public static class BlessedCrystal {

        private static final int xpBonusDrop = 5; //bonus xp to drop on kill

        public static void swordOnDeath(ServerPlayer player, ItemStack tool, DamageSource source, LivingEntity target) {
            int xp = target.getExperienceReward(player.level(), player);
            player.level().addFreshEntity(new ExperienceOrb(player.level(), target.getX(), target.getY(), target.getZ(), xp));
        }

        public static void toolOnBreakBlock(ServerPlayer player, ItemStack tool, BlockPos pos, BlockState state) {
            //test if the block name contains the word ore or if it drops experience, if so, drop extra experience
            if (state.getBlock().getName().getString().toLowerCase().contains("ore")) {
                //good
            } else if(state.getBlock() instanceof DropExperienceBlock) {
                //good
            } else {
                return;
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
            LOOT_TABLES_REGISTRY.get( state.getBlock().getLootTable().get() ).ifPresent(lootTable -> {
                state.getDrops(new LootParams.Builder(player.level()).withLuck(player.getLuck())
                ).forEach(stack -> {
                    player.level().addFreshEntity(new ItemEntity(player.level(), pos.getX(), pos.getY(), pos.getZ(), stack));
                });
            });
        }

    }

    /**
     * Fully charged strikes arc lightning to nearby mobs. Applicable tools vein-mine matching
     * blocks; the tool must be the right one for the job, so pickaxes chain ore and axes chain logs.
     */
    public static class ElectricCrystal {

        private static final float CHARGE_THRESHOLD = 0.9F;
        private static final double CHAIN_RADIUS = 8.0;
        private static final int MAX_CHAIN_TARGETS = 8;
        private static final float CHAIN_DAMAGE = 4.0F;
        private static final int MAX_VEIN_BLOCKS = 8;

        public static void swordOnHurt(ServerPlayer player, ItemStack tool, Entity target) {
            if (!isFullyCharged(player)) return;
            if (!(player.level() instanceof ServerLevel level)) return;

            strike(level, player, target.blockPosition());

            AABB area = target.getBoundingBox().inflate(CHAIN_RADIUS);
            List<Mob> chained = level.getEntitiesOfClass(Mob.class, area, mob -> mob != target && mob.isAlive());
            chained.sort(Comparator.comparingDouble(mob -> mob.distanceToSqr(target)));

            int struck = 0;
            for (Mob mob : chained) {
                if (struck >= MAX_CHAIN_TARGETS) break;
                strike(level, player, mob.blockPosition());
                mob.hurtServer(level, level.damageSources().lightningBolt(), CHAIN_DAMAGE);
                struck++;
            }
        }

        /**
         * Visual only; damage is applied directly so the bolt cannot start fires or convert mobs.
         */
        private static void strike(ServerLevel level, ServerPlayer player, BlockPos pos) {
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
            if (bolt == null) return;
            bolt.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            bolt.setVisualOnly(true);
            bolt.setCause(player);
            level.addFreshEntity(bolt);
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
                    strike(level, player, neighbour);
                    level.destroyBlock(neighbour, true, player, 512);
                }
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

    }

    /**
     * Weapons stack poison the harder the swing lands. Tools dissolve a cuboid of blocks over the
     * following seconds, with only a small chance of any given block yielding its loot.
     */
    public static class ToxicCrystal {

        private static final float CHARGE_THRESHOLD = 0.9F;
        private static final int POISON_DURATION = 100;
        private static final int MAX_POISON_AMPLIFIER = 3;

        private static final int AREA_WIDTH = 5;
        private static final int AREA_HEIGHT = 5;
        private static final int AREA_DEPTH = 5;
        private static final int BELOW = 3;
        private static final float LOOT_CHANCE = 0.25F;
        private static final float SKIP_CHANCE = 0.15F;
        private static final int MAX_DISSOLVE_SECOND = 5;

        private static final Map<ServerLevel, Set<BlockPos>> dissolvedMap = new HashMap<>();

        public static void swordOnHurt(ServerPlayer player, ItemStack tool, Entity target) {
            if (!(target instanceof LivingEntity living)) return;
            if (!isFullyCharged(player)) return;

            MobEffectInstance existing = living.getEffect(MobEffects.POISON);
            int amplifier = (existing==null ? 0 : Math.min(existing.getAmplifier() + 1, MAX_POISON_AMPLIFIER));
            living.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION, amplifier), player);
        }

        /**
         * Marks a 5x5x5 volume sitting three blocks below the broken block and one above it.
         */
        public static void toolOnBreakBlock(ServerPlayer player, ItemStack tool, BlockPos pos, BlockState state) {
            ServerLevel level = player.level();

            BlockPos corner = pos.offset(-(AREA_WIDTH / 2), -BELOW, -(AREA_DEPTH / 2));
            for (BlockPos afflicted : ShapeGen.cuboid(corner, AREA_WIDTH, AREA_HEIGHT, AREA_DEPTH)) {
                if (afflicted.equals(pos)) continue;
                if (level.getBlockState(afflicted).isAir()) continue;
                dissolvedMap.computeIfAbsent(player.level(), k -> new HashSet<>()).add(afflicted.immutable());
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
                level.sendParticles(ParticleTypes.COMPOSTER,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    1, 0.25, 0.1, 0.25, 0.0);
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

        public static void toolOnMineBlock(ServerPlayer player, ItemStack tool, BlockState state) {
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

    public static class EncasedBone {

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

        public static void toolOnMineBlock(ServerPlayer player, ItemStack tool, BlockState state) {
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

    public static class OvergrownBone {

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

        public static void toolOnMineBlock(ServerPlayer player, ItemStack tool, BlockState state) {
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

    public static class SpiritedBone {

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

        public static void toolOnMineBlock(ServerPlayer player, ItemStack tool, BlockState state) {
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

    public static class ToxicBone {

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

        public static void toolOnMineBlock(ServerPlayer player, ItemStack tool, BlockState state) {
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

    public static class EnderBone {

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

        public static void toolOnMineBlock(ServerPlayer player, ItemStack tool, BlockState state) {
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

}

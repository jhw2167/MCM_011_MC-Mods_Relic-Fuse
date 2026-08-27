package com.holybuckets.relicfuse.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.Map;

/**
 * Rising witch-purple miasma for blocks queued to dissolve. Bubbles are rare and the witch spell
 * particles ramp in the longer a block has been contaminated.
 */
public class ToxicEffect {

    public static int MIASMA_FROM = 0x8A2BE2;
    public static int MIASMA_TO = 0x6ABF1E;
    public static float MIASMA_SCALE = 1.1f;
    public static int TINT_COLOR = 0x9B30D9;

    public static double BUBBLE_CHANCE = .74;
    public static double WITCH_CHANCE_MAX = .80;
    public static double WITCH_RAMP_TICKS = 201.0;
    public static double WITCH_SECOND_FACTOR = .80;
    public static double DUST_CHANCE = .45;
    public static double TINT_CHANCE = .60;

    public static double SPREAD_XZ = 0.30;
    public static double BASE_HEIGHT = 1.0;
    public static double WITCH_LIFT = 0.30;
    public static double WITCH_LIFT_HIGH = 0.50;

    public static double BUBBLE_SPEED = 0.10;
    public static double WITCH_SPEED = 0.05;
    public static double WITCH_SPEED_HIGH = 0.03;
    public static double DUST_DRIFT = 0.01;

    public static int MAX_AGE = 400;

    private static ParticleOptions MIASMA_DUST;
    private static ParticleOptions MIASMA_TINT;

    private static final Map<ServerLevel, Map<BlockPos, Integer>> AGES = new HashMap<>();

    public static void contaminate(ServerLevel level, BlockPos pos) {
        AGES.computeIfAbsent(level, k -> new HashMap<>()).putIfAbsent(pos.immutable(), 0);
    }

    public static void forget(ServerLevel level, BlockPos pos) {
        Map<BlockPos, Integer> ages = AGES.get(level);
        if (ages != null) ages.remove(pos);
    }

    public static void clear(ServerLevel level) {
        AGES.remove(level);
    }

    public static void emit(ServerLevel level, BlockPos pos) {
        Map<BlockPos, Integer> ages = AGES.computeIfAbsent(level, k -> new HashMap<>());
        int age = ages.merge(pos.immutable(), 1, Integer::sum);
        if (age > MAX_AGE) {
            ages.remove(pos);
            return;
        }
        emit(level, pos, age);
    }

    public static void emit(ServerLevel level, BlockPos pos, int particleTick) {

        RandomSource random = level.getRandom();
        double witchChance = Math.min(WITCH_CHANCE_MAX, particleTick / WITCH_RAMP_TICKS);

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + BASE_HEIGHT;
        double cz = pos.getZ() + 0.5;

        if (random.nextDouble() < BUBBLE_CHANCE) {
            rise(level, ParticleTypes.BUBBLE_COLUMN_UP,
                rxz(random, cx), cy, rxz(random, cz), BUBBLE_SPEED);
        }

        if (random.nextDouble() < witchChance) {
            rise(level, ParticleTypes.WITCH,
                rxz(random, cx), cy + random.nextDouble() * WITCH_LIFT, rxz(random, cz), WITCH_SPEED);
        }

        if (random.nextDouble() < witchChance * WITCH_SECOND_FACTOR) {
            rise(level, ParticleTypes.WITCH,
                rxz(random, cx), cy + random.nextDouble() * WITCH_LIFT_HIGH, rxz(random, cz), WITCH_SPEED_HIGH);
        }

        if (random.nextDouble() < DUST_CHANCE) {
            level.sendParticles(miasmaDust(),
                cx, cy + 0.1, cz, 1, SPREAD_XZ, 0.15, SPREAD_XZ, DUST_DRIFT);
        }

        if (random.nextDouble() < TINT_CHANCE) {
            level.sendParticles(miasmaTint(),
                cx, cy + 0.2, cz, 1, SPREAD_XZ, 0.20, SPREAD_XZ, 0.0);
        }
    }

    public static void burst(ServerLevel level, BlockPos pos) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;

        level.sendParticles(ParticleTypes.WITCH, cx, cy, cz, 8, 0.25, 0.25, 0.25, 0.02);
        level.sendParticles(miasmaDust(), cx, cy, cz, 12, 0.30, 0.30, 0.30, 0.0);
        forget(level, pos);
    }

    private static void rise(ServerLevel level, ParticleOptions particle, double x, double y, double z, double speed) {
        level.sendParticles(particle, x, y, z, 0, 0.0, 1.0, 0.0, speed);
    }

    private static double rxz(RandomSource random, double center) {
        return center + (random.nextDouble() - 0.5) * SPREAD_XZ * 2.0;
    }

    private static ParticleOptions miasmaDust() {
        if (MIASMA_DUST == null) {
            MIASMA_DUST = new DustColorTransitionOptions(MIASMA_FROM, MIASMA_TO, MIASMA_SCALE);
        }
        return MIASMA_DUST;
    }

    private static ParticleOptions miasmaTint() {
        if (MIASMA_TINT == null) {
            MIASMA_TINT = ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, TINT_COLOR);
        }
        return MIASMA_TINT;
    }

}

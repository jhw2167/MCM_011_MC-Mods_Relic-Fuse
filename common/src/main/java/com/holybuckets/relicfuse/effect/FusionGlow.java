package com.holybuckets.relicfuse.effect;

import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.relicfuse.component.FusionComponent;
import com.holybuckets.relicfuse.core.ManagedPlayerFusions;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


public class FusionGlow {

    public static int EMIT_INTERVAL = 3;
    public static int PARTICLE_COUNT = 2;
    public static double SPREAD = 0.08;
    public static double DRIFT = 0.0;

    public static double HAND_FORWARD = 0.85;
    public static double HAND_LATERAL = 0.42;
    public static double HAND_DROP = 0.38;

    public static void init(EventRegistrar reg) {
        reg.registerOnServerLevelTick(FusionGlow::onServerLevelTick);
    }

    private static void onServerLevelTick(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (serverLevel.getGameTime() % EMIT_INTERVAL != 0) return;
        if (serverLevel.players().isEmpty()) return;

        HBUtil.PlayerUtil.getAllPlayers().stream()
            .filter(ManagedPlayerFusions::readyToFuse).forEach(player -> {
            emit(serverLevel, player);
        });
    }

    private static void emit(ServerLevel level, ServerPlayer player)
    {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 right = new Vec3(-look.z, 0.0, look.x).normalize();

        DustParticleOptions dust = dustFor(player.getOffhandItem());
        if (dust == null) return;
        Vec3 leftPos = handPos(eye, look, right, -1.0);
        Vec3 rightPos = handPos(eye, look, right, 1.0);

        level.sendParticles(
            dust,
            leftPos.x, leftPos.y, leftPos.z,
            PARTICLE_COUNT,
            SPREAD, SPREAD, SPREAD,
            DRIFT
        );

        level.sendParticles(
            dust,
            rightPos.x, rightPos.y, rightPos.z,
            PARTICLE_COUNT,
            SPREAD, SPREAD, SPREAD,
            DRIFT
        );
    }

    public static DustParticleOptions dustFor(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;

        DustParticleOptions own = FusionColors.getDust(stack.getItem());
        if (own != null) return own;

        Item modifier = FusionComponent.getModifier(stack);
        return modifier == null ? null : FusionColors.getDust(modifier);
    }

    private static Vec3 handPos(Vec3 eye, Vec3 look, Vec3 right, double side) {
        return eye.add(look.scale(HAND_FORWARD)).add(right.scale(HAND_LATERAL * side)).add(0.0, -HAND_DROP, 0.0);
    }

}

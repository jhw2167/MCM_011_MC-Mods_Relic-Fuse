package com.holybuckets.relicfuse.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.holybuckets.foundation.client.ClientEventRegistrar;
import com.holybuckets.foundation.event.custom.ClientTickEvent;
import com.holybuckets.foundation.event.custom.SimpleMessageEvent;
import com.holybuckets.foundation.event.custom.TickType;
import com.holybuckets.foundation.networking.SimpleStringMessage;
import com.holybuckets.relicfuse.LoggerProject;
import com.holybuckets.relicfuse.core.FusionManager;
import com.holybuckets.relicfuse.effect.ModEffects;
import com.holybuckets.relicfuse.item.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class FusionClientManager {

    /** Assigned in {@link #init} so registry-backed entries are resolved after bootstrap. */
    public static ParticleOptions HAND_GLOW_PARTICLE;
    public static ParticleOptions FUSION_TRAIL_PARTICLE;
    public static ParticleOptions EXPLOSION_PARTICLE;
    public static SoundEvent ANCIENT_POWER_AMBIENT;
    public static SoundEvent ON_FUSE_SOUND;

    public static int HAND_GLOW_COLOR = 0xC9A227;
    public static float HAND_GLOW_SCALE = 0.8f;
    public static float EXPLOSION_VOLUME = 0.6f;
    public static float EXPLOSION_PITCH = 1.4f;

    public static final int RISE_TICKS = 20;
    public static final int ORBIT_TICKS = 40;
    public static final int MAGNET_TICKS = 32;
    public static final int TOTAL_TICKS = RISE_TICKS + ORBIT_TICKS + MAGNET_TICKS;

    public static final double START_RADIUS = 0.55;
    public static final double END_RADIUS = 0.04;
    public static final double RISE_HEIGHT = 0.35;

    public static final double HAND_FORWARD = 0.85;
    public static final double HAND_LATERAL = 0.42;
    public static final double HAND_DROP = 0.2;
    public static final double ORBIT_REVOLUTIONS = 3.5;

    public static double SPIN_START = 14.0;
    public static double SPIN_END = 190.0;

    public static double SHAKE_MAX_DEGREES = 40.0;
    public static double SHAKE_SPEED = 0.55;

    public static double TILT_MAX_DEGREES = 15.0;
    public static double TILT_SPEED = 0.42;

    public static double MAGNET_RADIUS = 0.42;
    public static double MAGNET_PULSES = 1.0;
    public static double MAGNET_REVOLUTIONS = 1.5;

    public static double MAGNET_SHAKE = 0.45;

    public static double MAGNET_EASE = 0.35;

    public static float ITEM_SCALE = 0.75f;
    public static int ANIMATION_LIGHT = 15728880;

    private static final ItemStackRenderState TOOL_RENDER_STATE = new ItemStackRenderState();
    private static final ItemStackRenderState FUSABLE_RENDER_STATE = new ItemStackRenderState();

    private static final int HAND_GLOW_INTERVAL = 3;
    private static final int AMBIENT_SOUND_INTERVAL = 60;

    private static FusionAnimation active;

    public static void init(ClientEventRegistrar reg) {
        HAND_GLOW_PARTICLE = new DustParticleOptions(HAND_GLOW_COLOR, HAND_GLOW_SCALE);
        FUSION_TRAIL_PARTICLE = ParticleTypes.ENCHANT;
        EXPLOSION_PARTICLE = ParticleTypes.EXPLOSION_EMITTER;
        ANCIENT_POWER_AMBIENT = SoundEvents.BEACON_ACTIVATE;
        ON_FUSE_SOUND = SoundEvents.BEACON_ACTIVATE;

        reg.registerOnSimpleMessage(FusionManager.FUSE_START, FusionClientManager::onFuseStart);
        reg.registerOnSimpleMessage(FusionManager.TOTEM_USED, FusionClientManager::onTotemUsed);
        reg.registerOnClientTick(TickType.ON_SINGLE_TICK, FusionClientManager::onClientTick);
    }

    public static int TOTEM_PARTICLE_COUNT = 30;
    public static float TOTEM_VOLUME = 1.0f;
    public static float TOTEM_PITCH = 1.0f;


    private static void onTotemUsed(SimpleMessageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        mc.gameRenderer.displayItemActivation(new ItemStack(ModItems.ancientTotem.get()));

        for (int i = 0; i < TOTEM_PARTICLE_COUNT; i++) {
            mc.level.addParticle(ParticleTypes.TOTEM_OF_UNDYING,
                mc.player.getX() + (mc.level.getRandom().nextDouble() - 0.5) * 1.6,
                mc.player.getY() + mc.level.getRandom().nextDouble() * 2.0,
                mc.player.getZ() + (mc.level.getRandom().nextDouble() - 0.5) * 1.6,
                (mc.level.getRandom().nextDouble() - 0.5) * 0.4,
                mc.level.getRandom().nextDouble() * 0.4,
                (mc.level.getRandom().nextDouble() - 0.5) * 0.4);
        }

        mc.level.playLocalSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
            SoundEvents.TOTEM_USE, SoundSource.PLAYERS, TOTEM_VOLUME, TOTEM_PITCH, false);
    }

    private static void onFuseStart(SimpleMessageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        JsonObject json;
        try {
            json = JsonParser.parseString(event.getMessage().content).getAsJsonObject();
        } catch (Exception e) {
            LoggerProject.logError("011002", "Malformed fuse_start payload: " + event.getMessage().content);
            return;
        }

        ItemStack tool = stackOf(json, FusionManager.KEY_TOOL);
        ItemStack fusable = stackOf(json, FusionManager.KEY_FUSABLE);
        if (tool.isEmpty() || fusable.isEmpty()) return;

        mc.player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        mc.player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);

        active = new FusionAnimation(mc.level, mc.player, tool, fusable, event.getMessage().content);
    }


    public static void submitAnimation(PoseStack poseStack, SubmitNodeCollector collector, Vec3 cameraPos) {
        FusionAnimation animation = active;
        if (animation == null || poseStack == null || collector == null || cameraPos == null) return;

        try {
            float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
            animation.submit(poseStack, collector, cameraPos, partialTick);
        } catch (Exception ex) {
            active = null;
            LoggerProject.logError("011011", "FusionClientManager: error rendering fusion animation. " + ex);
        }
    }

    private static void onClientTick(ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        if (mc.player.hasEffect(ModEffects.ancientPower) ) {

            if( mc.player.tickCount % HAND_GLOW_INTERVAL == 0)
                emitHandGlow(mc.level, mc.player);
            if( mc.player.tickCount % AMBIENT_SOUND_INTERVAL == 0)
                mc.level.playSound(mc.player, mc.player.blockPosition(), ANCIENT_POWER_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        if (active != null && active.tick()) {
            active = null;
        }
    }

    /**
     * Hand positions are estimated from eye position and look vector; the player model exposes no
     * world-space hand anchor outside of the render pass.
     */
    private static void emitHandGlow(ClientLevel level, Player player) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 right = new Vec3(-look.z, 0.0, look.x).normalize();

        spawnGlow(level, handPos(eye, look, right, 1.0));
        spawnGlow(level, handPos(eye, look, right, -1.0));
    }

    private static Vec3 handPos(Vec3 eye, Vec3 look, Vec3 right, double side) {
        return eye.add(look.scale(HAND_FORWARD)).add(right.scale(HAND_LATERAL * side)).add(0.0, -HAND_DROP, 0.0);
    }

    private static void spawnGlow(ClientLevel level, Vec3 pos) {
        double jx = (level.getRandom().nextDouble() - 0.5) * 0.12;
        double jy = (level.getRandom().nextDouble() - 0.5) * 0.12;
        double jz = (level.getRandom().nextDouble() - 0.5) * 0.12;
        level.addParticle(HAND_GLOW_PARTICLE, pos.x + jx, pos.y + jy, pos.z + jz, 0.0, 0.01, 0.0);
    }

    private static ItemStack stackOf(JsonObject json, String key) {
        if (!json.has(key)) return ItemStack.EMPTY;
        Identifier id = Identifier.parse(json.get(key).getAsString());
        Item item = BuiltInRegistries.ITEM.getOptional(id).orElse(null);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    /**
     * Make the fused items do a little dance before being fused.
     */
    /**
     * Drawn straight into the entity pass rather than spawned as ItemEntities, because the item
     * renderer ignores entity rotation. Positions and angles are kept per tick with a previous
     * value so the render pass can interpolate.
     */
    private static class FusionAnimation {

        private final ClientLevel level;
        private final Player player;
        private final ItemStack tool;
        private final ItemStack fusable;
        private final Vec3 origin;
        private final Vec3 forward;
        private final Vec3 right;
        private final String payload;
        private int ticks;

        private Vec3 toolPos;
        private Vec3 toolPosOld;
        private Vec3 fusablePos;
        private Vec3 fusablePosOld;

        private float spin;
        private float spinOld;
        private float roll;
        private float rollOld;
        private float tilt;
        private float tiltOld;

        FusionAnimation(ClientLevel level, Player player, ItemStack tool, ItemStack fusable, String payload) {
            this.level = level;
            this.player = player;
            this.payload = payload;
            this.tool = tool.copy();
            this.fusable = fusable.copy();

            // Flattened so pitch does not drag the ritual to the player's feet, and frozen at spawn
            // so turning mid animation does not drag the items with you.
            Vec3 look = player.getLookAngle();
            this.forward = new Vec3(look.x, 0.0, look.z).normalize();
            this.right = new Vec3(-forward.z, 0.0, forward.x).normalize();

            this.origin = player.getEyePosition()
                .add(forward.scale(HAND_FORWARD))
                .add(0.0, -HAND_DROP, 0.0);

            this.toolPos = origin;
            this.toolPosOld = origin;
            this.fusablePos = origin;
            this.fusablePosOld = origin;
        }

        /** @return true when the animation has finished and should be released. */
        boolean tick() {
            ticks++;
            if (ticks >= TOTAL_TICKS) {
                detonate();
                return true;
            }

            double rise = Math.min(1.0, (double) ticks / RISE_TICKS);
            Vec3 centre = origin.add(0.0, RISE_HEIGHT * easeOut(rise), 0.0);

            double orbitT = Mth.clamp((double) (ticks - RISE_TICKS) / ORBIT_TICKS, 0.0, 1.0);
            double magnetT = Mth.clamp(
                (double) (ticks - RISE_TICKS - ORBIT_TICKS) / MAGNET_TICKS, 0.0, 1.0);
            boolean magnetising = magnetT > 0.0;

            double radius;
            if (magnetising) {
                double pulse = Math.abs(Math.sin(magnetT * Math.PI * MAGNET_PULSES));
                radius = END_RADIUS + pulse * MAGNET_RADIUS * (1.0 - magnetT);
            } else {
                radius = Mth.lerp(easeIn(orbitT), START_RADIUS, END_RADIUS);
            }

            // easeOut leaves the magnet phase turning at roughly the orbit's rate and lets it coast
            // down, so the circling does not visibly stall the instant the phase flips.
            double angle = (orbitT * ORBIT_REVOLUTIONS + easeOut(magnetT) * MAGNET_REVOLUTIONS)
                * Math.PI * 2.0;

            toolPosOld = toolPos;
            fusablePosOld = fusablePos;
            toolPos = place(centre, angle, radius);
            fusablePos = place(centre, angle + Math.PI, radius);

            // settle runs 0 to 1 over the first slice of the magnet phase. The spin decays across
            // it instead of stopping dead, and the shake eases down to its resting level on the
            // same curve so both changes land together.
            double settle = magnetising ? easeOut(Math.min(1.0, magnetT / MAGNET_EASE)) : 0.0;

            double intensity = magnetising
                ? Mth.lerp(settle, 1.0, MAGNET_SHAKE)
                : orbitT * orbitT;

            spinOld = spin;
            rollOld = roll;
            tiltOld = tilt;

            double spinRate = SPIN_START + (SPIN_END - SPIN_START) * (magnetising ? 1.0 : intensity);
            spin += (float) (spinRate * (1.0 - settle));

            roll = (float) (Math.sin(ticks * SHAKE_SPEED) * SHAKE_MAX_DEGREES * intensity);
            tilt = (float) Mth.clamp(
                Math.sin(ticks * TILT_SPEED + 1.3) * TILT_MAX_DEGREES * intensity,
                -TILT_MAX_DEGREES, TILT_MAX_DEGREES);

            return false;
        }

        /**
         * Offsets are built from the player's own right and forward vectors rather than world X and
         * Z, so angle 0 is always the main hand side no matter which way the player faces.
         */
        private Vec3 place(Vec3 centre, double angle, double radius) {
            return centre
                .add(right.scale(Math.cos(angle) * radius))
                .add(forward.scale(Math.sin(angle) * radius));
        }

        void submit(PoseStack poseStack, SubmitNodeCollector collector, Vec3 cameraPos, float partialTick) {
            float renderSpin = Mth.rotLerp(partialTick, spinOld, spin);
            float renderRoll = Mth.lerp(partialTick, rollOld, roll);
            float renderTilt = Mth.lerp(partialTick, tiltOld, tilt);

            draw(poseStack, collector, cameraPos, TOOL_RENDER_STATE, tool,
                toolPosOld.lerp(toolPos, partialTick), renderSpin, renderRoll, renderTilt);
            draw(poseStack, collector, cameraPos, FUSABLE_RENDER_STATE, fusable,
                fusablePosOld.lerp(fusablePos, partialTick), -renderSpin, -renderRoll, -renderTilt);
        }

        private void draw(PoseStack poseStack, SubmitNodeCollector collector, Vec3 cameraPos,
                          ItemStackRenderState renderState, ItemStack stack, Vec3 pos,
                          float itemSpin, float itemRoll, float itemTilt) {
            if (stack.isEmpty()) return;

            Minecraft.getInstance().getItemModelResolver()
                .updateForNonLiving(renderState, stack, ItemDisplayContext.GROUND, player);
            if (renderState.isEmpty()) return;

            poseStack.pushPose();
            poseStack.translate(pos.x - cameraPos.x, pos.y - cameraPos.y, pos.z - cameraPos.z);
            poseStack.mulPose(Axis.YP.rotationDegrees(itemSpin));
            poseStack.mulPose(Axis.XP.rotationDegrees(itemTilt));
            poseStack.mulPose(Axis.ZP.rotationDegrees(itemRoll));
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

            renderState.submit(poseStack, collector, ANIMATION_LIGHT, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        private void detonate() {
            Vec3 centre = origin.add(0.0, RISE_HEIGHT, 0.0);
            level.addParticle(EXPLOSION_PARTICLE, centre.x, centre.y, centre.z, 0.0, 0.0, 0.0);
            level.playLocalSound(centre.x, centre.y, centre.z, ON_FUSE_SOUND, SoundSource.PLAYERS,
                EXPLOSION_VOLUME, EXPLOSION_PITCH, false);
            SimpleStringMessage.createAndFire(player, FusionManager.FUSE_COMPLETE, payload);
        }
    }

    private static double easeIn(double t) {
        return t * t;
    }

    private static double easeOut(double t) {
        return 1.0 - (1.0 - t) * (1.0 - t);
    }

}

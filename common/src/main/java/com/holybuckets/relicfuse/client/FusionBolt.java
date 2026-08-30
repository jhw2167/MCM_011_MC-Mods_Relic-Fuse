package com.holybuckets.relicfuse.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.holybuckets.foundation.GeneralConfig;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.LoggerBase;
import com.holybuckets.foundation.client.ClientEventRegistrar;
import com.holybuckets.foundation.event.balm.client.ConnectedToServerEvent;
import com.holybuckets.foundation.event.custom.ClientTickEvent;
import com.holybuckets.foundation.event.custom.SimpleMessageEvent;
import com.holybuckets.foundation.event.custom.TickType;
import com.holybuckets.relicfuse.core.FusionAbilities;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4fc;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.holybuckets.foundation.HBUtil.*;

public class FusionBolt {

    public static int DEFAULT_COLOR = 0xE6E6FF;
    public static int DEFAULT_LIFE_TICKS = 60;

    public static float VANILLA_HEIGHT = 128.0f;
    public static float SPREAD = 0.22f;

    /** Half width of the outermost pass in blocks, held constant regardless of bolt length. */
    public static float RADIUS_SCALE = 0.25f;
    public static float BASE_ALPHA = 0.30f;

    public static int MAX_ACTIVE = 32;

    public static double CENTER_FACTOR = 0.5;

    public static boolean isIgnored(Entity entity) {
        return entity != null;
    }

    private static final List<Bolt> ACTIVE = new ArrayList<>();
    private static long nextSeed = 1L;
    private static Player player;
    private static GeneralConfig CONFIG;

    public static void init(ClientEventRegistrar reg) {
        reg.registerOnClientTick(TickType.ON_SINGLE_TICK, FusionBolt::onClientTick);
        reg.registerOnSimpleMessage(FusionAbilities.ElectricCrystal.STRIKE_MSG_ID, FusionBolt::onLightningAttack);
        reg.registerOnSimpleMessage(FusionAbilities.ElectricCrystal.BREAK_MSG_ID, FusionBolt::onLightningBreakBlock);
        reg.registerOnConnectedToServer(FusionBolt::connectedToServer);
        CONFIG = GeneralConfig.getInstance();
    }

    public static void connectedToServer(ConnectedToServerEvent event) {
        player = event.getClient().player;
        //ACTIVE.clear();
    }

    /**
     * Creates a bolt from one entity to the next, using the data from a SimpleStringMessage
     * @param message
     */
    private static void onLightningAttack(SimpleMessageEvent message)
    {
        if (player == null) return;
        JsonObject obj = JsonParser.parseString(message.getContent()).getAsJsonObject();
        int id1 = obj.get("from").getAsInt();
        int id2 = obj.get("to").getAsInt();
        String levelId = obj.get("level").getAsString();
        Level serverLevel = LevelUtil.toClientLevel(levelId);
        Level level = player.level();
        if(serverLevel == null || serverLevel!=level) return;

        Entity e1 = level.getEntity(id1);
        Entity e2 = level.getEntity(id2);
        if(e1 == null || e2 == null) return;
        if(isIgnored(e1) || isIgnored(e2)) return;
        strike(e1, e2, DEFAULT_COLOR, DEFAULT_LIFE_TICKS);
        level.playLocalSound(e1, SoundEvents.LIGHTNING_BOLT_IMPACT, e1.getSoundSource(), 1.0f, 1.0f);

    }


    //which passes two blockPos instead of entity ids. resolve with HBUtil.BlockUtil.toPos
    private static void onLightningBreakBlock(SimpleMessageEvent message) {

        if (player == null) return;
        JsonObject obj = JsonParser.parseString(message.getContent()).getAsJsonObject();
        String levelId = obj.get("level").getAsString();
        Level serverLevel = LevelUtil.toClientLevel(levelId);
        Level level = player.level();
        if(serverLevel == null || serverLevel!=level) return;

        BlockPos p1 = HBUtil.BlockUtil.stringToBlockPos(obj.get("from").getAsString());
        BlockPos p2 = HBUtil.BlockUtil.stringToBlockPos(obj.get("to").getAsString());
        Vec3 from = Vec3.atCenterOf(p1);
        Vec3 to = Vec3.atCenterOf(p2);
        strike(from, to, DEFAULT_COLOR, DEFAULT_LIFE_TICKS);
        level.playLocalSound(from.x, from.y, from.z, SoundEvents.LIGHTNING_BOLT_IMPACT, player.getSoundSource(), 1.0f, 1.0f, false);
    }

    public static void strike(Vec3 from, Vec3 to) {
        strike(from, to, DEFAULT_COLOR, DEFAULT_LIFE_TICKS);
    }

    public static void strike(Vec3 from, Vec3 to, int rgb) {
        strike(from, to, rgb, DEFAULT_LIFE_TICKS);
    }

    public static void strike(Vec3 from, Vec3 to, int rgb, int lifeTicks) {
        if (from == null || to == null || lifeTicks <= 0) return;
        add(new Bolt(null, null, from, to, rgb, lifeTicks, nextSeed++));
    }

    public static void strike(Entity from, Entity to) {
        strike(from, to, DEFAULT_COLOR, DEFAULT_LIFE_TICKS);
    }

    /**
     * Holds both entities so the bolt tracks them each frame and dies with either one.
     */
    public static void strike(Entity from, Entity to, int rgb, int lifeTicks) {
        if (from == null || to == null || lifeTicks <= 0) return;
        if (isIgnored(from) || isIgnored(to)) return;
        add(new Bolt(from, to, from.position(), to.position(), rgb, lifeTicks, nextSeed++));
    }

    private static void add(Bolt bolt) {
        if (ACTIVE.size() >= MAX_ACTIVE) ACTIVE.remove(0);
        ACTIVE.add(bolt);
    }

    public static void clear() {
        ACTIVE.clear();
    }

    public static class Bolt {

        final Entity fromEntity;
        final Entity toEntity;
        final Vec3 fromPos;
        final Vec3 toPos;
        final int color;
        final int lifeTicks;
        final long seed;
        int age;

        Bolt(Entity fromEntity, Entity toEntity, Vec3 fromPos, Vec3 toPos, int color, int lifeTicks, long seed) {
            this.fromEntity = fromEntity;
            this.toEntity = toEntity;
            this.fromPos = fromPos;
            this.toPos = toPos;
            this.color = color;
            this.lifeTicks = lifeTicks;
            this.seed = seed;
            this.age = 0;
        }

        Vec3 start() {
            return anchor(fromEntity, fromPos);
        }

        Vec3 end() {
            return anchor(toEntity, toPos);
        }

        boolean expired() {
            if (age >= lifeTicks) return true;
            if (isDead(fromEntity)) return true;
            return isDead(toEntity);
        }
    }

    private static Vec3 anchor(Entity entity, Vec3 fallback) {
        if (entity == null) return fallback;
        return entity.position().add(0.0, entity.getBbHeight() * CENTER_FACTOR, 0.0);
    }

    private static boolean isDead(Entity entity) {
        return entity != null && (entity.isRemoved() || !entity.isAlive());
    }

    private static void onClientTick(ClientTickEvent event) {
        if (Minecraft.getInstance().level == null) {
            ACTIVE.clear();
            return;
        }
        Iterator<Bolt> it = ACTIVE.iterator();
        while (it.hasNext()) {
            Bolt bolt = it.next();
            bolt.age++;
            if (bolt.expired()) it.remove();
        }
    }

    /**
     * Called from LevelRendererMixin at the tail of submitEntities, so the lightning render type is
     * drawn inside the entity pass where its shader uniforms are bound.
     */
    public static void submitBolts(PoseStack poseStack, SubmitNodeCollector collector, Vec3 cameraPos) {
        if (ACTIVE.isEmpty() || poseStack == null || collector == null || cameraPos == null) return;
        try {
            renderBolts(poseStack, collector, cameraPos);
        } catch (Exception ex) {
            LoggerBase.logWarning(null, "011010",
                "FusionBolt: error rendering bolts, clearing active list. " + ex);
        }
    }

    /**
     * Vanilla lightning render method!
     */
    private static void renderBolts(PoseStack poseStack, SubmitNodeCollector collector, Vec3 cameraPos) {

        RenderType renderType = RenderTypes.lightning();

        for (Bolt bolt : ACTIVE) {
            Vec3 start = bolt.start();
            Vec3 end = bolt.end();
            Vec3 axis = start.subtract(end);
            double length = axis.length();
            if (length < 1.0E-4) continue;

            Vec3 dir = axis.scale(1.0 / length);
            float scale = (float) (length / VANILLA_HEIGHT);

            poseStack.pushPose();
            poseStack.translate(
                end.x - cameraPos.x,
                end.y - cameraPos.y,
                end.z - cameraPos.z
            );
            poseStack.mulPose(new Quaternionf().rotationTo(
                0.0f, 1.0f, 0.0f, (float) dir.x, (float) dir.y, (float) dir.z));
            poseStack.scale(scale, scale, scale);

            final float boltScale = scale;
            collector.submitCustomGeometry(poseStack, renderType,
                (pose, buffer) -> drawBolt(pose.pose(), buffer, bolt, boltScale));

            poseStack.popPose();
        }
    }

    /**
     * The pose is scaled by length/128, which would shrink the bolt's width along with it, so the
     * radius divides that back out and stays a fixed number of blocks at any range.
     */
    private static void drawBolt(Matrix4fc poseMatrix, VertexConsumer buffer, Bolt bolt, float boltScale) {

        long seed = bolt.seed * 31L + bolt.age;
        float radiusScale = boltScale > 1.0E-5f ? RADIUS_SCALE / boltScale : RADIUS_SCALE;

        float[] xOffs = new float[8];
        float[] zOffs = new float[8];
        float xOff = 0.0F;
        float zOff = 0.0F;
        RandomSource random = RandomSource.createThreadLocalInstance(seed);

        for (int h = 7; h >= 0; --h) {
            xOffs[h] = xOff;
            zOffs[h] = zOff;
            xOff += (random.nextInt(11) - 5) * SPREAD;
            zOff += (random.nextInt(11) - 5) * SPREAD;
        }

        float boltRed = ((bolt.color >> 16) & 0xFF) / 255.0F;
        float boltGreen = ((bolt.color >> 8) & 0xFF) / 255.0F;
        float boltBlue = (bolt.color & 0xFF) / 255.0F;
        //float boltAlpha = BASE_ALPHA * fade;
        float boltAlpha = BASE_ALPHA;

        for (int r = 0; r < 4; ++r) {
            RandomSource randomx = RandomSource.createThreadLocalInstance(seed);

            for (int p = 0; p < 3; ++p) {
                int hs = 7;
                int ht = 0;
                if (p > 0) {
                    hs = 7 - p;
                }

                if (p > 0) {
                    ht = hs - 2;
                }

                float xo0 = xOffs[hs] - xOff;
                float zo0 = zOffs[hs] - zOff;

                for (int h = hs; h >= ht; --h) {
                    float xo1 = xo0;
                    float zo1 = zo0;
                    if (p == 0) {
                        xo0 += (randomx.nextInt(11) - 5) * SPREAD;
                        zo0 += (randomx.nextInt(11) - 5) * SPREAD;
                    } else {
                        xo0 += (randomx.nextInt(31) - 15) * SPREAD;
                        zo0 += (randomx.nextInt(31) - 15) * SPREAD;
                    }

                    float rr1 = (0.1F + (float) r * 0.2F) * radiusScale;
                    if (p == 0) {
                        rr1 *= (float) h * 0.1F + 1.0F;
                    }

                    float rr2 = (0.1F + (float) r * 0.2F) * radiusScale;
                    if (p == 0) {
                        rr2 *= ((float) h - 1.0F) * 0.1F + 1.0F;
                    }

                    quad(poseMatrix, buffer, xo0, zo0, h, xo1, zo1, boltRed, boltGreen, boltBlue, boltAlpha, rr1, rr2, false, false, true, false);
                    quad(poseMatrix, buffer, xo0, zo0, h, xo1, zo1, boltRed, boltGreen, boltBlue, boltAlpha, rr1, rr2, true, false, true, true);
                    quad(poseMatrix, buffer, xo0, zo0, h, xo1, zo1, boltRed, boltGreen, boltBlue, boltAlpha, rr1, rr2, true, true, false, true);
                    quad(poseMatrix, buffer, xo0, zo0, h, xo1, zo1, boltRed, boltGreen, boltBlue, boltAlpha, rr1, rr2, false, true, false, false);
                }
            }
        }
    }

    private static void quad(Matrix4fc pose, VertexConsumer buffer, float xo0, float zo0, int h, float xo1, float zo1,
                             float boltRed, float boltGreen, float boltBlue, float boltAlpha,
                             float rr1, float rr2, boolean px1, boolean pz1, boolean px2, boolean pz2) {
        buffer.addVertex(pose, xo0 + (px1 ? rr2 : -rr2), (float) (h * 16), zo0 + (pz1 ? rr2 : -rr2)).setColor(boltRed, boltGreen, boltBlue, boltAlpha);
        buffer.addVertex(pose, xo1 + (px1 ? rr1 : -rr1), (float) ((h + 1) * 16), zo1 + (pz1 ? rr1 : -rr1)).setColor(boltRed, boltGreen, boltBlue, boltAlpha);
        buffer.addVertex(pose, xo1 + (px2 ? rr1 : -rr1), (float) ((h + 1) * 16), zo1 + (pz2 ? rr1 : -rr1)).setColor(boltRed, boltGreen, boltBlue, boltAlpha);
        buffer.addVertex(pose, xo0 + (px2 ? rr2 : -rr2), (float) (h * 16), zo0 + (pz2 ? rr2 : -rr2)).setColor(boltRed, boltGreen, boltBlue, boltAlpha);
    }

}

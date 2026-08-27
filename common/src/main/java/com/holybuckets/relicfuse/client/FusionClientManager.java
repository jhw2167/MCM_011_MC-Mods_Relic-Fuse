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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
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
import net.minecraft.world.entity.item.ItemEntity;
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
    public static final int ORBIT_TICKS = 60;
    public static final int TOTAL_TICKS = RISE_TICKS + ORBIT_TICKS;

    public static final double START_RADIUS = 0.55;
    public static final double END_RADIUS = 0.04;
    public static final double RISE_HEIGHT = 0.35;

    /** Forward offset of the palm-up hands from the eye, and their drop below eye level. */
    public static final double HAND_FORWARD = 0.85;
    public static final double HAND_LATERAL = 0.42;
    public static final double HAND_DROP = 0.38;
    public static final double ORBIT_REVOLUTIONS = 3.5;
    public static final double WOBBLE_AMPLITUDE = 0.22;
    public static final double WOBBLE_FREQUENCY = 34.0;

    private static final int HAND_GLOW_INTERVAL = 3;
    private static final int AMBIENT_SOUND_INTERVAL = 60;
    private static int clientEntityId = -20000;

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

    /**
     * Mirrors the vanilla totem sequence: the item flash is driven client side so the ancient totem
     * texture is shown rather than the vanilla totem hardcoded by the entity event.
     */
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

        if (active != null) active.discard();
        active = new FusionAnimation(mc.level, mc.player, tool, fusable, event.getMessage().content);
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
    private static class FusionAnimation {

        private final ClientLevel level;
        private final Player player;
        private final ItemEntity toolEntity;
        private final ItemEntity fusableEntity;
        private final Vec3 origin;
        private final String payload;
        private int ticks;

        FusionAnimation(ClientLevel level, Player player, ItemStack tool, ItemStack fusable, String payload) {
            this.level = level;
            this.player = player;
            this.payload = payload;
            Vec3 look = player.getLookAngle();
            this.origin = player.getEyePosition().add(look.scale(HAND_FORWARD)).add(0.0, -HAND_DROP, 0.0);
            this.toolEntity = spawn(tool);
            this.fusableEntity = spawn(fusable);
        }

        private ItemEntity spawn(ItemStack stack) {
            ItemEntity entity = new ItemEntity(level, origin.x, origin.y, origin.z, stack.copy());
            entity.setId(clientEntityId--);
            entity.setNoGravity(true);
            entity.setNeverPickUp();
            entity.setUnlimitedLifetime();
            entity.setDeltaMovement(Vec3.ZERO);
            level.addEntity(entity);
            return entity;
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

            double t = Math.max(0.0, (double) (ticks - RISE_TICKS) / ORBIT_TICKS);
            double radius = Mth.lerp(easeIn(t), START_RADIUS, END_RADIUS);
            double angle = t * ORBIT_REVOLUTIONS * Math.PI * 2.0;
            double wobble = Math.sin(t * WOBBLE_FREQUENCY * t) * WOBBLE_AMPLITUDE * t;

            place(toolEntity, centre, angle, radius, wobble);
            place(fusableEntity, centre, angle + Math.PI, radius, -wobble);

            if (t > 0.0 && ticks % 2 == 0) {
                level.addParticle(FUSION_TRAIL_PARTICLE, centre.x, centre.y, centre.z, 0.0, 0.0, 0.0);
            }
            return false;
        }

        private void place(ItemEntity entity, Vec3 centre, double angle, double radius, double wobble) {
            double x = centre.x + Math.cos(angle) * radius;
            double z = centre.z + Math.sin(angle) * radius;
            double y = centre.y + wobble;
            entity.setPos(x, y, z);
            entity.xOld = x;
            entity.yOld = y;
            entity.zOld = z;
            entity.setDeltaMovement(Vec3.ZERO);
        }

        private void detonate() {
            Vec3 centre = origin.add(0.0, RISE_HEIGHT, 0.0);
            level.addParticle(EXPLOSION_PARTICLE, centre.x, centre.y, centre.z, 0.0, 0.0, 0.0);
            level.playLocalSound(centre.x, centre.y, centre.z, ON_FUSE_SOUND, SoundSource.PLAYERS,
                EXPLOSION_VOLUME, EXPLOSION_PITCH, false);
            discard();
            SimpleStringMessage.createAndFire(player, FusionManager.FUSE_COMPLETE, payload);
        }

        void discard() {
            toolEntity.discard();
            fusableEntity.discard();
        }
    }

    private static double easeIn(double t) {
        return t * t;
    }

    private static double easeOut(double t) {
        return 1.0 - (1.0 - t) * (1.0 - t);
    }

}

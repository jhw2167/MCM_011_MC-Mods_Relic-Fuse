package com.holybuckets.relicfuse.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/**
 * SimpleAnimatedParticle walks the sprite set in its own tick, which is what SuspendedTownParticle
 * never did; it locks one frame for the particle's whole life.
 */
public class AncientPowerParticle extends SimpleAnimatedParticle {

    public static int LIFETIME_MIN = 20;
    public static int LIFETIME_VARIANCE = 20;
    public static float PARTICLE_GRAVITY = -0.02f;
    public static float QUAD_SIZE = 0.12f;
    public static float DRAG = 0.94f;

    protected AncientPowerParticle(ClientLevel level, double x, double y, double z,
                                   double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z, sprites, PARTICLE_GRAVITY);

        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        this.friction = DRAG;
        this.quadSize = QUAD_SIZE;
        this.lifetime = LIFETIME_MIN + this.random.nextInt(LIFETIME_VARIANCE);
        this.setSpriteFromAge(sprites);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz, RandomSource random) {
            return new AncientPowerParticle(level, x, y, z, dx, dy, dz, sprites);
        }
    }
}

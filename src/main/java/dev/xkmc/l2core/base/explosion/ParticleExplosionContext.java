package dev.xkmc.l2core.base.explosion;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;

public record ParticleExplosionContext(ParticleOptions small,
									   ParticleOptions large,
									   SoundEvent sound) {
}

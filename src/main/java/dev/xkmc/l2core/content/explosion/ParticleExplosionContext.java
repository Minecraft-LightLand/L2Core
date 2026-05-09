package dev.xkmc.l2core.content.explosion;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.random.WeightedList;

public record ParticleExplosionContext(ParticleOptions particle,
                                       Holder<SoundEvent> sound,
                                       WeightedList<ExplosionParticleInfo> blockParticles) {

	private static final WeightedList<ExplosionParticleInfo> DEFAULT_EXPLOSION_BLOCK_PARTICLES = WeightedList.<ExplosionParticleInfo>builder()
			.add(new ExplosionParticleInfo(ParticleTypes.POOF, 0.5F, 1.0F))
			.add(new ExplosionParticleInfo(ParticleTypes.SMOKE, 1.0F, 1.0F))
			.build();

	public static ParticleExplosionContext of(float radius) {
		return new ParticleExplosionContext(
				radius < 2 ? ParticleTypes.EXPLOSION : ParticleTypes.EXPLOSION_EMITTER,
				SoundEvents.GENERIC_EXPLODE, DEFAULT_EXPLOSION_BLOCK_PARTICLES
		);
	}

}

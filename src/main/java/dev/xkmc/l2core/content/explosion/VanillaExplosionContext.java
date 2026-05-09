package dev.xkmc.l2core.content.explosion;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;

import javax.annotation.Nullable;

public record VanillaExplosionContext(@Nullable Entity entity, @Nullable DamageSource source,
                                      @Nullable ExplosionDamageCalculator calculator,
                                      boolean fire, Explosion.BlockInteraction type) {

	public VanillaExplosionContext(ServerLevel level, @Nullable Entity entity, @Nullable DamageSource source,
	                               @Nullable ExplosionDamageCalculator calculator,
	                               boolean fire, Level.ExplosionInteraction type) {
		this(entity, source, calculator, fire, getType(level, entity, type));
	}

	private static Explosion.BlockInteraction getType(ServerLevel level, @Nullable Entity entity, Level.ExplosionInteraction type) {
		var ans = level.getGameRules().get(GameRules.TNT_EXPLOSION_DROP_DECAY) ?
				Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
		return switch (type) {
			case NONE -> Explosion.BlockInteraction.KEEP;
			case BLOCK -> ans;
			case MOB -> net.neoforged.neoforge.event.EventHooks.canEntityGrief(level, entity)
					? ans : Explosion.BlockInteraction.KEEP;
			case TNT -> ans;
			case TRIGGER -> Explosion.BlockInteraction.TRIGGER_BLOCK;
		};
	}

}

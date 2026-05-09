package dev.xkmc.l2core.content.explosion;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerExplosion;

public class BaseExplosion extends ServerExplosion {

	public final BaseExplosionContext base;
	public final ModExplosionContext mod;
	public final VanillaExplosionContext mc;
	public final ParticleExplosionContext particle;

	public BaseExplosion(BaseExplosionContext base, VanillaExplosionContext mc, ModExplosionContext mod, ParticleExplosionContext particle) {
		super(base.level(), mc.entity(), mc.source(), mc.calculator(), base.pos(), base.r(), mc.fire(), mc.type());
		this.base = base;
		this.mod = mod;
		this.mc = mc;
		this.particle = particle;
	}

	/**
	 * return false to cancel hurt
	 */
	public boolean hurtEntity(Entity entity) {
		return mod.hurtEntity(entity);
	}

}

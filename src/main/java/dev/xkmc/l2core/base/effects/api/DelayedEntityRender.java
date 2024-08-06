package dev.xkmc.l2core.base.effects.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public record DelayedEntityRender(LivingEntity entity, IconRenderRegion region, ResourceLocation rl,
								  double xo, double yo, double zo,
								  float tx, float ty, float tw, float th) {


	public static DelayedEntityRender icon(LivingEntity entity, ResourceLocation rl) {
		return icon(entity, IconRenderRegion.identity(), rl);
	}

	public static DelayedEntityRender icon(LivingEntity entity, IconRenderRegion r, ResourceLocation rl) {
		return new DelayedEntityRender(entity, r, rl, 0, 0, 1, 1);
	}

	public DelayedEntityRender(LivingEntity entity, IconRenderRegion region, ResourceLocation rl,
							   float tx, float ty, float tw, float th) {
		this(entity, region, rl, entity.xOld, entity.yOld, entity.zOld, tx, ty, tw, th);
	}

	public DelayedEntityRender resize(IconRenderRegion r) {
		return new DelayedEntityRender(entity, r.resize(region), rl, xo, yo, zo, tx, ty, tw, th);
	}

}
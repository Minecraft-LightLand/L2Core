package dev.xkmc.l2core.base.effects.api;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public record DelayedEntityRender(
		LivingEntity entity, IconRenderRegion region, ResourceLocation rl,
		double xo, double yo, double zo,
		float tx, float ty, float tw, float th
) implements IDelayedRender {

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

	@Override
	public Vec3 pos(float partial) {
		LivingEntity entity = entity();
		float f = entity.getBbHeight() / 2;
		double x0 = Mth.lerp(partial, xo(), entity.getX());
		double y0 = Mth.lerp(partial, yo(), entity.getY());
		double z0 = Mth.lerp(partial, zo(), entity.getZ());
		var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		Vec3 offset = dispatcher.getRenderer(entity).getRenderOffset(entity, partial);
		double x = x0 + offset.x();
		double y = y0 + offset.y() + f;
		double z = z0 + offset.z();
		return new Vec3(x, y, z);
	}
}
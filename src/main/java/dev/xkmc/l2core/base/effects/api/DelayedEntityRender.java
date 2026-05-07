package dev.xkmc.l2core.base.effects.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public record DelayedEntityRender(
		LivingEntityRenderState entity, IconRenderRegion region, Identifier rl,
		float tx, float ty, float tw, float th
) implements IDelayedRender {

	public static DelayedEntityRender icon(LivingEntityRenderState entity, Identifier rl) {
		return icon(entity, IconRenderRegion.identity(), rl);
	}

	public static DelayedEntityRender icon(LivingEntityRenderState entity, IconRenderRegion r, Identifier rl) {
		return new DelayedEntityRender(entity, r, rl, 0, 0, 1, 1);
	}

	public DelayedEntityRender resize(IconRenderRegion r) {
		return new DelayedEntityRender(entity, r.resize(region), rl, tx, ty, tw, th);
	}

	@Override
	public Vec3 pos() {
		float f = entity.boundingBoxHeight / 2;
		var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		Vec3 offset = dispatcher.getRenderer(entity).getRenderOffset(entity);
		double x = entity.x + offset.x();
		double y = entity.y + offset.y() + f;
		double z = entity.z + offset.z();
		return new Vec3(x, y, z);
	}

}
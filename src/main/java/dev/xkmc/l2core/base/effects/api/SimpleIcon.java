package dev.xkmc.l2core.base.effects.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public record SimpleIcon(
		ResourceLocation rl, IconRenderRegion region,
		float tx, float ty, float tw, float th, Vec3 pos
) implements IDelayedRender {

	public static SimpleIcon of(ResourceLocation rl, Vec3 pos) {
		return new SimpleIcon(rl, IconRenderRegion.identity(), 0, 0, 1, 1, pos);
	}

	@Override
	public Vec3 pos(float partial) {
		return pos;
	}

}

package dev.xkmc.l2core.base.effects.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public interface IDelayedRender {
	ResourceLocation rl();

	IconRenderRegion region();

	float tx();

	float ty();

	float tw();

	float th();

	Vec3 pos(float partial);

}

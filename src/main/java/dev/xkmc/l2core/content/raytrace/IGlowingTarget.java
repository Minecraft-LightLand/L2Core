package dev.xkmc.l2core.content.raytrace;

import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface IGlowingTarget {

	@OnlyIn(Dist.CLIENT)
	int getDistance(ItemStack stack);

}

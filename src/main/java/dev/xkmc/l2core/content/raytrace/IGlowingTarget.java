package dev.xkmc.l2core.content.raytrace;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IGlowingTarget extends IClientTickItem {

	int getDistance(ItemStack stack);

	default void clientMainHandTick(Level level, Player player, ItemStack itemStack) {
		RayTraceUtil.clientUpdateTarget(player, getDistance(itemStack));
	}

}

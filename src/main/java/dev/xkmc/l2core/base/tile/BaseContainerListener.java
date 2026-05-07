package dev.xkmc.l2core.base.tile;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface BaseContainerListener extends ContainerListener {

	void notifyTile();

	@Override
	default void slotChanged(AbstractContainerMenu container, int slotIndex, ItemStack itemStack) {
		notifyTile();
	}

}

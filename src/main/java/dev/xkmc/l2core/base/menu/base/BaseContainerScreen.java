package dev.xkmc.l2core.base.menu.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class BaseContainerScreen<T extends BaseContainerMenu<T>> extends AbstractContainerScreen<T> {

	public BaseContainerScreen(T cont, Inventory plInv, Component title) {
		super(cont, plInv, title, 176, cont.getLayout().getHeight());
		this.inventoryLabelY = menu.getLayout().getPlInvY() - 11;
	}

	protected LayoutRenderer getRenderer() {
		return new LayoutRenderer(menu.getLayout(), menu.getLayoutId(), this);
	}

	protected boolean click(int btn) {
		if (menu.clickMenuButton(menu.player, btn) && Minecraft.getInstance().gameMode != null) {
			Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, btn);
			return true;
		}
		return false;
	}

}

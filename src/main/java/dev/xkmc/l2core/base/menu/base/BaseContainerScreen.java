package dev.xkmc.l2core.base.menu.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class BaseContainerScreen<T extends BaseContainerMenu<T>> extends AbstractContainerScreen<T> {

	public BaseContainerScreen(T cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
		this.imageHeight = menu.getLayout().getHeight();
		this.inventoryLabelY = menu.getLayout().getPlInvY() - 11;
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float partial) {
		super.render(g, mx, my, partial);
		renderTooltip(g, mx, my);
	}

	protected MenuLayoutConfig.ScreenRenderer getRenderer() {
		return menu.getLayout().getRenderer(menu.getLayoutId(), this);
	}

	protected boolean click(int btn) {
		if (menu.clickMenuButton(menu.player, btn) && Minecraft.getInstance().gameMode != null) {
			Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, btn);
			return true;
		}
		return false;
	}

}

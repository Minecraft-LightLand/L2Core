package dev.xkmc.l2core.base.overlay;

import dev.xkmc.l2core.init.L2LibraryConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;

import java.util.List;

public abstract class InfoSideBar<S extends SideBar.Signature<S>> extends SideBar<S> implements IGuiOverlay {

	public InfoSideBar(float duration, float ease) {
		super(duration, ease);
	}

	@Override
	public void render(ExtendedGui gui, GuiGraphics g, float partialTick, int width, int height) {
		if (!ease(gui.getGuiTicks() + partialTick))
			return;
		var text = getText();
		if (text.isEmpty()) return;
		int anchor = L2LibraryConfig.CLIENT.infoAnchor.get();
		int y = height * anchor / 2;
		int w = (int) (width * L2LibraryConfig.CLIENT.infoMaxWidth.get());
		new TextBox(g, 0, anchor, getXOffset(width), y, w)
				.renderLongText(Minecraft.getInstance().font, text);
	}

	protected abstract List<Component> getText();

	@Override
	protected int getXOffset(int width) {
		float progress = (max_ease - ease_time) / max_ease;
		return Math.round(-progress * width / 2 + 8);
	}

}


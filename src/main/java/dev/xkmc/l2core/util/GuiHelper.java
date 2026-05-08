package dev.xkmc.l2core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class GuiHelper {

	public static void tooltip(GuiGraphicsExtractor g, List<Component> text, int x, int y) {
		List<ClientTooltipComponent> list = new ArrayList<>();
		for (var e : text) {
			list.add(ClientTooltipComponent.create(e.getVisualOrderText()));
		}
		g.tooltip(Minecraft.getInstance().font, list, x, y, DefaultTooltipPositioner.INSTANCE, null);
	}

	public static void blit(GuiGraphicsExtractor g, Identifier id, int x, int y, int u, int v, int w, int h) {
		g.blit(RenderPipelines.GUI_TEXTURED, id, x, y, u, v, w, h, 256, 256);
	}

}

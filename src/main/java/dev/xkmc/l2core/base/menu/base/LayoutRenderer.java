package dev.xkmc.l2core.base.menu.base;

import dev.xkmc.l2core.util.GuiHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.Identifier;

public class LayoutRenderer {

	private final MenuLayoutConfig parent;
	public final int x, y, w, h;
	public final Screen scr;

	public final Identifier id;

	public LayoutRenderer(MenuLayoutConfig parent, Identifier id, Screen gui, int x, int y, int w, int h) {
		this.parent = parent;
		scr = gui;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.id = id;
	}

	public LayoutRenderer(MenuLayoutConfig parent, Identifier id, AbstractContainerScreen<?> scrIn) {
		this.parent = parent;
		x = scrIn.getLeftPos();
		y = scrIn.getTopPos();
		w = scrIn.getImageWidth();
		h = scrIn.getImageHeight();
		scr = scrIn;
		this.id = id;
	}

	public MenuLayoutConfig parent() {
		return parent;
	}

	public MenuLayoutConfig.Rect getComp(String key) {
		return parent.getComp(key);
	}

	public MenuLayoutConfig.Rect getSide(String key) {
		return parent.getSide(key);
	}

	public void blit(GuiGraphicsExtractor g, int x, int y, int u, int v, int w, int h) {
		GuiHelper.blit(g, MenuLayoutConfig.getTexture(id), x, y, u, v, w, h);
	}

	/**
	 * Draw a side sprite on the location specified by the component
	 */
	public void draw(GuiGraphicsExtractor g, String c, String s) {
		MenuLayoutConfig.Rect cr = getComp(c);
		MenuLayoutConfig.Rect sr = getSide(s);
		blit(g, x + cr.x, y + cr.y, sr.x, sr.y, sr.w, sr.h);
	}

	/**
	 * Draw a side sprite on the location specified by the component with offsets
	 */
	public void draw(GuiGraphicsExtractor g, String c, String s, int xoff, int yoff) {
		MenuLayoutConfig.Rect cr = getComp(c);
		MenuLayoutConfig.Rect sr = getSide(s);
		blit(g, x + cr.x + xoff, y + cr.y + yoff, sr.x, sr.y, sr.w, sr.h);
	}

	/**
	 * Draw a side sprite on the location specified by the component. Draw partially
	 * from bottom to top
	 */
	public void drawBottomUp(GuiGraphicsExtractor g, String c, String s, int prog, int max) {
		if (prog == 0 || max == 0)
			return;
		MenuLayoutConfig.Rect cr = getComp(c);
		MenuLayoutConfig.Rect sr = getSide(s);
		int dh = sr.h * prog / max;
		blit(g, x + cr.x, y + cr.y + sr.h - dh, sr.x, sr.y + sr.h - dh, sr.w, dh);
	}

	/**
	 * Draw a side sprite on the location specified by the component. Draw partially
	 * from left to right
	 */
	public void drawLeftRight(GuiGraphicsExtractor g, String c, String s, int prog, int max) {
		if (prog == 0 || max == 0)
			return;
		MenuLayoutConfig.Rect cr = getComp(c);
		MenuLayoutConfig.Rect sr = getSide(s);
		int dw = sr.w * prog / max;
		blit(g, x + cr.x, y + cr.y, sr.x, sr.y, dw, sr.h);
	}

	/**
	 * fill an area with a sprite, repeat as tiles if not enough, start from lower
	 * left corner
	 */
	public void drawLiquid(GuiGraphicsExtractor g, String c, double per, int height, int sw, int sh) {
		MenuLayoutConfig.Rect cr = getComp(c);
		int base = cr.y + height;
		int h = (int) Math.round(per * height);
		circularBlit(g, x + cr.x, base - h, 0, -h, cr.w, h, sw, sh);
	}

	/**
	 * bind texture, draw background color, and GUI background
	 */
	public void start(GuiGraphicsExtractor g) {
		blit(g, x, y, 0, 0, w, h);
	}

	private void circularBlit(GuiGraphicsExtractor g, int sx, int sy, int ix, int iy, int w, int h, int iw, int ih) {
		int x0 = ix, yb = iy, x1 = w, x2 = sx;
		while (x0 < 0)
			x0 += iw;
		while (yb < ih)
			yb += ih;
		while (x1 > 0) {
			int dx = Math.min(x1, iw - x0);
			int y0 = yb, y1 = h, y2 = sy;
			while (y1 > 0) {
				int dy = Math.min(y1, ih - y0);
				blit(g, x2, y2, x0, y0, x1, y1);
				y1 -= dy;
				y0 += dy;
				y2 += dy;
			}
			x1 -= dx;
			x0 += dx;
			x2 += dx;
		}
	}

}

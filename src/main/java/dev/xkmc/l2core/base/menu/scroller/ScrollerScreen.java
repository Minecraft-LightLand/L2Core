package dev.xkmc.l2core.base.menu.scroller;

public interface ScrollerScreen {

	ScrollerMenu getMenu();

	int getGuiLeft();

	int getGuiTop();

	void scrollTo(int i);
}

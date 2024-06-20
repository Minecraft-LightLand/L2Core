package dev.xkmc.l2core.base.menu.base;

import dev.xkmc.l2core.init.L2LibReg;
import net.minecraft.resources.ResourceLocation;

public record SpriteManager(ResourceLocation id) {

	public SpriteManager(String modid, String path) {
		this(new ResourceLocation(modid, path));
	}

	public MenuLayoutConfig get() {
		return L2LibReg.MENU_LAYOUT.get(id);
	}

}

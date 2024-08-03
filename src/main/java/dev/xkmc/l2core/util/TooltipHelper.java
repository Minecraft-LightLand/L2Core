package dev.xkmc.l2core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.function.Consumer;

public record TooltipHelper(Level level, boolean shift, boolean alt) {

	public static void addClient(Item.TooltipContext ctx, TooltipFlag flag, Consumer<TooltipHelper> action) {
		if (!FMLEnvironment.dist.isClient()) return;
		if (ctx.registries() == null) return;
		var level = Minecraft.getInstance().level;
		if (level == null) return;
		action.accept(new TooltipHelper(level, Screen.hasShiftDown(), Screen.hasAltDown()));
	}

}

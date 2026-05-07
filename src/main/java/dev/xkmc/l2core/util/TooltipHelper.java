package dev.xkmc.l2core.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.function.Consumer;

public record TooltipHelper(Level level, boolean shift, boolean alt) {

	public static void addClient(Item.TooltipContext ctx, TooltipFlag flag, Consumer<TooltipHelper> action) {
		if (!FMLEnvironment.getDist().isClient()) return;
		if (ctx.registries() == null) return;
		var level = Minecraft.getInstance().level;
		if (level == null) return;
		action.accept(new TooltipHelper(level, hasShiftDown(), hasAltDown()));
	}


	public static boolean hasShiftDown() {
		return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 340)
				|| InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 344);
	}

	public static boolean hasAltDown() {
		return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 342)
				|| InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 346);
	}

}

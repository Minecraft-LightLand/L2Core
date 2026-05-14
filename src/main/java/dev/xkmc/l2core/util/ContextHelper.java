package dev.xkmc.l2core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

import java.util.List;

public class ContextHelper {

	public static List<ItemStack> resolve(Ingredient ing) {
		var level = Minecraft.getInstance().level;
		ContextMap ctx = level != null ? SlotDisplayContext.fromLevel(level) : ContextMap.EMPTY;
		return ing.display().resolveForStacks(ctx);
	}

}

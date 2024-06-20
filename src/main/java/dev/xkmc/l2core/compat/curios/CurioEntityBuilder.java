package dev.xkmc.l2core.compat.curios;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public record CurioEntityBuilder(
		ArrayList<ResourceLocation> entities,
		ArrayList<String> slots,
		ArrayList<SlotCondition> conditions) {
}

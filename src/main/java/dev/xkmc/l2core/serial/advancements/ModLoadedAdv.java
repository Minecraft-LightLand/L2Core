package dev.xkmc.l2core.serial.advancements;

import net.minecraft.advancements.Advancement;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.List;

public record ModLoadedAdv(String... modid) implements IAdvBuilder {

	@Override
	public void onBuild(String id, Advancement.Builder builder, List<ICondition> conditions) {
		for (var e : modid) {
			conditions.add(new ModLoadedCondition(e));
		}
	}

}

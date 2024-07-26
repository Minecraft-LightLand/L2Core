package dev.xkmc.l2core.serial.advancements;

import com.tterrag.registrate.providers.ProviderType;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.List;
import java.util.function.Supplier;

public record RewardBuilder(L2Registrate reg, int exp, ResourceKey<LootTable> loot,
							Supplier<LootTable.Builder> sup) implements IAdvBuilder {

	public RewardBuilder(L2Registrate reg, int exp, ResourceKey<LootTable> loot,
						 Supplier<LootTable.Builder> sup) {
		this.reg = reg;
		this.exp = exp;
		this.loot = loot;
		this.sup = sup;
		reg.addDataGenerator(ProviderType.LOOT, e -> e.addLootAction(LootContextParamSets.EMPTY,
				x -> x.accept(loot, sup.get())));
	}

	@Override
	public void onBuild(String id, Advancement.Builder builder, List<ICondition> conditions) {
		builder.rewards(AdvancementRewards.Builder.loot(loot).addExperience(exp).build());
	}

}

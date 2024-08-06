package dev.xkmc.l2core.serial.loot;

import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

import java.util.List;
import java.util.Optional;

public record LootHelper(RegistrateBlockLootTables pvd) {

	public <T> Holder<T> resolve(ResourceKey<T> key) {
		return pvd.getRegistries().holderOrThrow(key);
	}

	public EnchantmentPredicate hasEnch(ResourceKey<Enchantment> enchant, int min) {
		return new EnchantmentPredicate(resolve(enchant), MinMaxBounds.Ints.atLeast(min));
	}

	public LootItemCondition.Builder toolHasEnch(ResourceKey<Enchantment> enchant, int min) {
		return MatchTool.toolMatches(ItemPredicate.Builder.item().withSubPredicate(
				ItemSubPredicates.ENCHANTMENTS, ItemEnchantmentsPredicate.enchantments(
						List.of(hasEnch(enchant, min)))));
	}

	public LootItemCondition.Builder silk() {
		return toolHasEnch(Enchantments.SILK_TOUCH, 1);
	}

	public LootItemCondition.Builder intState(Block block, Property<Integer> prop, int val) {
		return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
				.setProperties(StatePropertiesPredicate.Builder.properties()
						.hasProperty(prop, val));
	}

	public <T extends Comparable<T> & StringRepresentable> LootItemCondition.Builder enumState(Block block, Property<T> prop, T val) {
		return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
				.setProperties(StatePropertiesPredicate.Builder.properties()
						.hasProperty(prop, val));
	}

	public LootItemCondition.Builder intRangeState(Block block, Property<Integer> prop, int min, int max) {
		return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
				.setProperties(Util.make(StatePropertiesPredicate.Builder.properties(),
						e -> e.matchers.add(new StatePropertiesPredicate.PropertyMatcher(prop.getName(),
								new StatePropertiesPredicate.RangedMatcher(
										Optional.of("" + min), Optional.of("" + max))))));
	}

	public LootItemFunction.Builder fortuneCount(int factor) {
		return ApplyBonusCount.addUniformBonusCount(resolve(Enchantments.FORTUNE), factor);
	}

	public LootItemCondition.Builder fortuneChance(int... denominators) {
		float[] fracs = new float[denominators.length];
		for (int i = 0; i < fracs.length; i++) fracs[i] = 1f / denominators[i];
		return BonusLevelTableCondition.bonusLevelFlatChance(resolve(Enchantments.FORTUNE), fracs);
	}

}

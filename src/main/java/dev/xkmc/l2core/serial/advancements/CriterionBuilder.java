package dev.xkmc.l2core.serial.advancements;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CriterionBuilder implements IAdvBuilder {

	@Deprecated
	public static CriterionBuilder none() {
		return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()));
	}

	public static CriterionBuilder item(Item item) {
		return one(InventoryChangeTrigger.TriggerInstance.hasItems(item));
	}

	public static CriterionBuilder item(TagKey<Item> item, DataComponentPredicate tag) {
		return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(item).hasComponents(tag).build()));
	}

	public static CriterionBuilder item(ItemLike item, DataComponentPredicate tag) {
		return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(item).hasComponents(tag).build()));
	}

	public static CriterionBuilder items(Item... item) {
		return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(item).build()));
	}

	public static CriterionBuilder item(TagKey<Item> item) {
		return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(item).build()));
	}

	public static CriterionBuilder book(Holder<Enchantment> enchantment) {
		return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()
				.withSubPredicate(ItemSubPredicates.STORED_ENCHANTMENTS,
						ItemEnchantmentsPredicate.storedEnchantments(List.of(
								new EnchantmentPredicate(enchantment, MinMaxBounds.Ints.ANY))))));
	}

	public static CriterionBuilder enchanted(Holder<Enchantment> enchantment) {
		return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()
				.withSubPredicate(ItemSubPredicates.ENCHANTMENTS,
						ItemEnchantmentsPredicate.enchantments(List.of(
								new EnchantmentPredicate(enchantment, MinMaxBounds.Ints.ANY))))));
	}

	public static CriterionBuilder enchanted(ItemLike item, Holder<Enchantment> enchantment) {
		return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(item)
				.withSubPredicate(ItemSubPredicates.ENCHANTMENTS,
						ItemEnchantmentsPredicate.enchantments(List.of(
								new EnchantmentPredicate(enchantment, MinMaxBounds.Ints.ANY))))));
	}

	public static CriterionBuilder enchanted(TagKey<Item> item, Holder<Enchantment> enchantment) {
		return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(item)
				.withSubPredicate(ItemSubPredicates.ENCHANTMENTS,
						ItemEnchantmentsPredicate.enchantments(List.of(
								new EnchantmentPredicate(enchantment, MinMaxBounds.Ints.ANY))))));
	}

	public static CriterionBuilder player(PlayerTrigger trigger) {
		return one(trigger.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty())));
	}

	public static CriterionBuilder one(Criterion<?> instance) {
		return new CriterionBuilder(RequirementsStrategy.AND).add(instance);
	}

	public enum RequirementsStrategy {
		AND, OR
	}

	public static CriterionBuilder and() {
		return new CriterionBuilder(RequirementsStrategy.AND);
	}

	public static CriterionBuilder or() {
		return new CriterionBuilder(RequirementsStrategy.OR);
	}

	private final RequirementsStrategy req;
	private final List<Pair<String, Criterion<?>>> list = new ArrayList<>();

	private CriterionBuilder(RequirementsStrategy req) {
		this.req = req;
	}

	public CriterionBuilder add(Criterion<?> instance) {
		return add(list.size() + "", instance);
	}

	public CriterionBuilder add(String str, Criterion<?> instance) {
		list.add(Pair.of(str, instance));
		return this;
	}

	public void onBuild(String id, Advancement.Builder builder, List<ICondition> conditions) {
		if (list.size() > 1) {
			var ids = list.stream().map(Pair::getFirst).toList();
			builder.requirements(req == RequirementsStrategy.AND ?
					AdvancementRequirements.allOf(ids) :
					AdvancementRequirements.anyOf(ids)
			);
		}
		for (var c : list) {
			builder.addCriterion(c.getFirst(), c.getSecond());
		}
	}

}

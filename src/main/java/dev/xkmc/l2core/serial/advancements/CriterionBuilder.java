package dev.xkmc.l2core.serial.advancements;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.*;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CriterionBuilder implements IAdvBuilder {

	public static class Provider {

		private final HolderGetter<Item> itemPvd;
		private final HolderGetter<Enchantment> enchPvd;

		public Provider(HolderLookup.Provider pvd) {
			this.itemPvd = pvd.lookupOrThrow(Registries.ITEM);
			this.enchPvd = pvd.lookupOrThrow(Registries.ENCHANTMENT);
		}

		@Deprecated
		public static CriterionBuilder none() {
			return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()));
		}

		public static CriterionBuilder item(Item item) {
			return one(InventoryChangeTrigger.TriggerInstance.hasItems(item));
		}

		public CriterionBuilder item(TagKey<Item> item, DataComponentMatchers tag) {
			return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(itemPvd, item).withComponents(tag).build()));
		}

		public CriterionBuilder item(ItemLike item, DataComponentMatchers tag) {
			return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(itemPvd, item).withComponents(tag).build()));
		}

		public CriterionBuilder items(Item... item) {
			return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(itemPvd, item).build()));
		}

		public CriterionBuilder item(TagKey<Item> item) {
			return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(itemPvd, item).build()));
		}

		public CriterionBuilder book(ResourceKey<Enchantment> enchantment) {
			return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()
					.withComponents(DataComponentMatchers.Builder.components().partial(
							DataComponentPredicates.STORED_ENCHANTMENTS,
							EnchantmentsPredicate.storedEnchantments(List.of(
									new EnchantmentPredicate(enchPvd.getOrThrow(enchantment), MinMaxBounds.Ints.ANY)))).build())));
		}

		public CriterionBuilder enchanted(ResourceKey<Enchantment> enchantment) {
			return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()
					.withComponents(DataComponentMatchers.Builder.components().partial(
							DataComponentPredicates.ENCHANTMENTS,
							EnchantmentsPredicate.enchantments(List.of(
									new EnchantmentPredicate(enchPvd.getOrThrow(enchantment), MinMaxBounds.Ints.ANY)))).build())));
		}

		public CriterionBuilder enchanted(ItemLike item, ResourceKey<Enchantment> enchantment) {
			return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(itemPvd, item)
					.withComponents(DataComponentMatchers.Builder.components().partial(
							DataComponentPredicates.ENCHANTMENTS,
							EnchantmentsPredicate.enchantments(List.of(
									new EnchantmentPredicate(enchPvd.getOrThrow(enchantment), MinMaxBounds.Ints.ANY)))).build())));
		}

		public CriterionBuilder enchanted(TagKey<Item> item, ResourceKey<Enchantment> enchantment) {
			return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(itemPvd, item)
					.withComponents(DataComponentMatchers.Builder.components().partial(
							DataComponentPredicates.ENCHANTMENTS,
							EnchantmentsPredicate.enchantments(List.of(
									new EnchantmentPredicate(enchPvd.getOrThrow(enchantment), MinMaxBounds.Ints.ANY)))).build())));
		}

		public static CriterionBuilder player(PlayerTrigger trigger) {
			return one(trigger.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty())));
		}

		public static CriterionBuilder one(Criterion<?> instance) {
			return new CriterionBuilder(RequirementsStrategy.AND).add(instance);
		}

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

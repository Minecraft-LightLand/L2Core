package dev.xkmc.l2core.serial.loot;

import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.providers.loot.RegistrateEntityLootTables;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.Optional;

public record LootHelper(HolderLookup.Provider pvd) {

	public LootHelper(RegistrateBlockLootTables pvd) {
		this(pvd.getRegistries());
	}

	public LootHelper(RegistrateEntityLootTables pvd) {
		this(pvd.getRegistries());
	}

	public <T> Holder<T> resolve(ResourceKey<T> key) {
		return pvd.holderOrThrow(key);
	}

	public LootPoolSingletonContainer.Builder<?> item(Item item) {
		return LootItem.lootTableItem(item);
	}

	public LootPoolSingletonContainer.Builder<?> item(Item item, int count) {
		return LootItem.lootTableItem(item)
				.apply(SetItemCountFunction.setCount(ConstantValue.exactly(count)));
	}

	public LootPoolSingletonContainer.Builder<?> item(Item item, int min, int max) {
		return LootItem.lootTableItem(item)
				.apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)));
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

	public LootItemFunction.Builder fortuneBin() {
		return fortuneBin(4 / 7f, 3);
	}

	public LootItemFunction.Builder fortuneBin(float chance, int count) {
		return ApplyBonusCount.addBonusBinomialDistributionCount(resolve(Enchantments.FORTUNE), chance, count);
	}

	public LootItemFunction.Builder lootCount(float factor) {
		return EnchantedCountIncreaseFunction.lootingMultiplier(pvd, UniformGenerator.between(0, factor));
	}

	public LootItemCondition.Builder lootChance(float base, float slope) {
		return LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(pvd, base, slope);
	}

	public LootItemCondition.Builder fire(boolean fire) {
		return LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
				EntityPredicate.Builder.entity().flags(
						EntityFlagsPredicate.Builder.flags().setOnFire(fire)
				).build());
	}

	public LootItemFunction.Builder smelt() {
		return SmeltItemFunction.smelted().when(fire(true));
	}

	public LootItemCondition.Builder damage(TagKey<DamageType> tag) {
		return DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
				.tag(TagPredicate.is(tag)));
	}

	public LootItemCondition.Builder entity(EntityType<?> type) {
		return LootItemEntityPropertyCondition.hasProperties(
				LootContext.EntityTarget.THIS,
				EntityPredicate.Builder.entity().entityType(
						EntityTypePredicate.of(type)));
	}

	public LootItemCondition.Builder entity(TagKey<EntityType<?>> tag) {
		return LootItemEntityPropertyCondition.hasProperties(
				LootContext.EntityTarget.THIS,
				EntityPredicate.Builder.entity().entityType(
						EntityTypePredicate.of(tag)));
	}

	public LootItemCondition.Builder killer(EntityType<?> type) {
		return LootItemEntityPropertyCondition.hasProperties(
				LootContext.EntityTarget.ATTACKER,
				EntityPredicate.Builder.entity().entityType(
						EntityTypePredicate.of(type)));
	}

	public LootItemCondition.Builder killer(TagKey<EntityType<?>> tag) {
		return LootItemEntityPropertyCondition.hasProperties(
				LootContext.EntityTarget.ATTACKER,
				EntityPredicate.Builder.entity().entityType(
						EntityTypePredicate.of(tag)));
	}

	public LootItemCondition.Builder killerItem(EquipmentSlot slot, Item item) {
		return LootItemEntityPropertyCondition.hasProperties(
				LootContext.EntityTarget.ATTACKER,
				EntityPredicate.Builder.entity().equipment(
						slot(slot, ItemPredicate.Builder.item().of(item)).build()).build());
	}

	public LootItemCondition.Builder killerItem(EquipmentSlot slot, TagKey<Item> item) {
		return LootItemEntityPropertyCondition.hasProperties(
				LootContext.EntityTarget.ATTACKER,
				EntityPredicate.Builder.entity().equipment(
						slot(slot, ItemPredicate.Builder.item().of(item)).build()).build());
	}

	private EntityEquipmentPredicate.Builder slot(EquipmentSlot slot, ItemPredicate.Builder item) {
		var b = EntityEquipmentPredicate.Builder.equipment();
		return switch (slot) {
			case MAINHAND -> b.mainhand(item);
			case OFFHAND -> b.offhand(item);
			case FEET -> b.feet(item);
			case LEGS -> b.legs(item);
			case CHEST -> b.chest(item);
			case HEAD -> b.head(item);
			case BODY -> b.body(item);
		};
	}

}

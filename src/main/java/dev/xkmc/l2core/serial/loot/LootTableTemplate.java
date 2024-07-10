package dev.xkmc.l2core.serial.loot;

import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Optional;

@SuppressWarnings("unused")
public class LootTableTemplate {

	public static LootPool.Builder getPool(int roll, int bonus) {
		return LootPool.lootPool().setRolls(ConstantValue.exactly(roll)).setBonusRolls(ConstantValue.exactly(0));
	}

	public static LootPoolSingletonContainer.Builder<?> getItem(Item item, int count) {
		return LootItem.lootTableItem(item)
				.apply(SetItemCountFunction.setCount(ConstantValue.exactly(count)));
	}

	public static LootPoolSingletonContainer.Builder<?> getItem(Item item, int min, int max) {
		return LootItem.lootTableItem(item)
				.apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)));
	}

	public static LootItemCondition.Builder byPlayer() {
		return LootItemKilledByPlayerCondition.killedByPlayer();
	}

	public static LootItemCondition.Builder chance(float chance) {
		return LootItemRandomChanceCondition.randomChance(chance);
	}

	public static LootItemBlockStatePropertyCondition.Builder withBlockState(Block block, Property<Integer> prop, int low, int high) {
		StatePropertiesPredicate.Builder builder = StatePropertiesPredicate.Builder.properties();
		builder.matchers.add(new StatePropertiesPredicate.PropertyMatcher(prop.getName(),
				new StatePropertiesPredicate.RangedMatcher(
						Optional.of(Integer.toString(low)),
						Optional.of(Integer.toString(high)))));
		return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(builder);
	}

	public static LootItemBlockStatePropertyCondition.Builder withBlockState(Block block, Property<Integer> prop, int val) {
		return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(
				StatePropertiesPredicate.Builder.properties().hasProperty(prop, val)
		);
	}

	public static LootItemBlockStatePropertyCondition.Builder withBlockState(Block block, Property<Boolean> prop, boolean val) {
		return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(
				StatePropertiesPredicate.Builder.properties().hasProperty(prop, val)
		);
	}

	public static LootItemBlockStatePropertyCondition.Builder withBlockState(Block block, Property<?> prop, String val) {
		return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(
				StatePropertiesPredicate.Builder.properties().hasProperty(prop, val)
		);
	}

	public static EnchantmentPredicate hasEnchantment(Holder<Enchantment> enchant, int min) {
		return new EnchantmentPredicate(enchant, MinMaxBounds.Ints.atLeast(min));
	}


}

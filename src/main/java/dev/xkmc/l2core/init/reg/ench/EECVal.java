package dev.xkmc.l2core.init.reg.ench;

import dev.xkmc.l2core.init.reg.simple.Val;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface EECVal<T> extends Val<DataComponentType<List<ConditionalEffect<T>>>> {

	static <T> void apply(List<ConditionalEffect<T>> list, LootContext ctx, Consumer<T> cons) {
		for (ConditionalEffect<T> e : list) {
			if (e.matches(ctx)) {
				cons.accept(e.effect());
			}
		}
	}

	static void iterateItem(ItemStack stack, BiConsumer<Holder<Enchantment>, Integer> cons) {
		ItemEnchantments enchs = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
		var lookup = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
		if (lookup != null) enchs = stack.getAllEnchantments(lookup);
		for (var entry : enchs.entrySet()) {
			cons.accept(entry.getKey(), entry.getIntValue());
		}
	}

	static LootContext itemContext(ServerLevel level, int lv, ItemStack stack) {
		LootParams lootparams = new LootParams.Builder(level)
				.withParameter(LootContextParams.TOOL, stack)
				.withParameter(LootContextParams.ENCHANTMENT_LEVEL, lv)
				.create(LootContextParamSets.ENCHANTED_ITEM);
		return new LootContext.Builder(lootparams).create(Optional.empty());
	}

	record Impl<T>(
			DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<T>>>> val
	) implements EECVal<T> {

		@Override
		public DataComponentType<List<ConditionalEffect<T>>> get() {
			return val.get();
		}

	}

	interface Num extends EECVal<EnchantmentValueEffect> {

		default float modify(ServerLevel level, ItemStack stack, float f) {
			MutableFloat val = new MutableFloat(f);
			EECVal.iterateItem(stack, (ench, lv) ->
					apply(ench.value().getEffects(get()), itemContext(level, lv, stack),
							eff -> val.setValue(eff.process(lv, level.getRandom(), val.getValue()))));
			return val.getValue();
		}

		record Impl(
				DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> val
		) implements Num {

			@Override
			public DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> get() {
				return val.get();
			}

		}

	}

	interface Flag extends Val<DataComponentType<Unit>> {

		default boolean isOn(ItemStack stack) {
			return EnchantmentHelper.has(stack, get());
		}

		record Impl<T>(
				DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> val
		) implements Flag {

			@Override
			public DataComponentType<Unit> get() {
				return val.get();
			}

		}

	}

	interface Special<T> extends Val<DataComponentType<List<T>>> {

		record Impl<T>(
				DeferredHolder<DataComponentType<?>, DataComponentType<List<T>>> val
		) implements Special<T> {

			@Override
			public DataComponentType<List<T>> get() {
				return val.get();
			}

		}

	}

}

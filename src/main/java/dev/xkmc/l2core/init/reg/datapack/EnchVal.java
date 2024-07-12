package dev.xkmc.l2core.init.reg.datapack;

import cpw.mods.util.Lazy;
import dev.xkmc.l2core.init.reg.simple.DCVal;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public interface EnchVal {

	ResourceKey<Enchantment> id();

	interface Impl extends EnchVal {
		Lazy<Builder> builder();
	}

	interface Flag extends EnchVal {
		DCVal<Unit> unit();
	}

	record Simple(ResourceKey<Enchantment> id, Lazy<Builder> builder) implements Impl {

	}

	record FlagImpl(DCVal<Unit> unit, ResourceKey<Enchantment> id, Lazy<Builder> builder) implements Flag, Impl {

	}

	class Builder {

		private TagKey<Item> supported, primary;
		private Enchantment.Cost min, max;
		private EquipmentSlotGroup group;
		private int weight = 1, maxLevel = 1, anvilCost = 1;
		@Nullable
		private TagKey<Enchantment> exclude;
		private final List<Consumer<Enchantment.Builder>> effects = new ArrayList<>();

		final List<TagKey<Enchantment>> tags = new ArrayList<>();

		Builder() {
			supported = primary = Tags.Items.ENCHANTABLES;
			min = max = new Enchantment.Cost(10, 5);
			group = EquipmentSlotGroup.ANY;
		}

		public Builder transform(UnaryOperator<Builder> func) {
			return func.apply(this);
		}

		public Builder items(TagKey<Item> items) {
			return items(items, items);
		}

		public Builder items(TagKey<Item> supported, TagKey<Item> primary) {
			this.supported = supported;
			this.primary = primary;
			return this;
		}

		public Builder cost(int base, int slope, int range) {
			min = new Enchantment.Cost(base, slope);
			max = new Enchantment.Cost(base + range, slope);
			return this;
		}

		public Builder weight(int weight) {
			this.weight = weight;
			return this;
		}

		public Builder anvilCost(int cost) {
			this.anvilCost = cost;
			return this;
		}

		public Builder maxLevel(int lv) {
			this.maxLevel = lv;
			return this;
		}

		public Builder group(EquipmentSlotGroup group) {
			this.group = group;
			return this;
		}

		public Builder exclusive(TagKey<Enchantment> tag) {
			this.exclude = tag;
			return this;
		}

		@SafeVarargs
		public final Builder tags(TagKey<Enchantment>... tags) {
			this.tags.addAll(List.of(tags));
			return this;
		}

		public Builder effect(Consumer<Enchantment.Builder> effect) {
			this.effects.add(effect);
			return this;
		}

		Enchantment build(BootstrapContext<Enchantment> ctx, ResourceLocation id) {
			var items = ctx.registryLookup(Registries.ITEM).orElseThrow();
			var enchs = ctx.registryLookup(Registries.ENCHANTMENT).orElseThrow();
			var ans = Enchantment.enchantment(Enchantment.definition(
					items.getOrThrow(supported),
					items.getOrThrow(primary),
					weight, maxLevel,
					min, max, anvilCost, group));
			if (exclude != null) ans.exclusiveWith(enchs.getOrThrow(exclude));
			for (var e : effects) e.accept(ans);
			return ans.build(id);
		}

	}

}

package dev.xkmc.l2core.init.reg.ench;

import com.tterrag.registrate.providers.RegistrateProvider;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.util.DataGenOnly;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public interface EnchVal {

	ResourceKey<Enchantment> id();

	@DataGenOnly
	Holder<Enchantment> datagenDirect(RegistrateProvider pvd);

	default Optional<Holder<Enchantment>> safeHolder() {
		return Optional.ofNullable(CommonHooks.resolveLookup(Registries.ENCHANTMENT)).flatMap(e -> e.get(id()));
	}

	default Holder<Enchantment> holder() {
		return safeHolder().orElseThrow();
	}

	default int getLv(ItemStack stack) {
		return safeHolder().map(stack::getEnchantmentLevel).orElse(0);
	}

	default int getLvIntrinsic(ItemStack stack) {
		return safeHolder().map(e -> stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).getLevel(e)).orElse(0);
	}

	interface Impl extends EnchVal {
		Lazy<Builder> builder();

		@Override
		default Holder<Enchantment> datagenDirect(RegistrateProvider pvd) {
			var val = builder().get().cache;
			if (val == null) throw new IllegalStateException("Enchantment is not built yet");
			return new DataGenHolder<>(id(), val);
		}

	}

	interface Flag extends EnchVal {
		EECVal.Flag unit();
	}

	interface Legacy<T extends LegacyEnchantment> extends EnchVal {

		DeferredHolder<LegacyEnchantment, T> legacy();

	}

	record Simple(ResourceKey<Enchantment> id, Lazy<Builder> builder) implements Impl {

	}

	record FlagImpl(EECVal.Flag unit, ResourceKey<Enchantment> id, Lazy<Builder> builder) implements Flag, Impl {

	}

	record LegacyImpl<T extends LegacyEnchantment>(
			ResourceKey<Enchantment> id, DeferredHolder<LegacyEnchantment, T> legacy, Lazy<Builder> builder
	) implements Legacy<T>, Impl {

	}

	class Builder {

		private HolderSetBuilder<Item> supported, primary;
		private Enchantment.Cost min, max;
		@Nullable
		private EquipmentSlotGroup group;
		private int weight = 1, maxLevel = 1, anvilCost = 1;
		@Nullable
		private HolderSetBuilder<Enchantment> exclude;
		private final List<Consumer<Enchantment.Builder>> effects = new ArrayList<>();

		public final ResourceLocation id;

		final List<TagKey<Enchantment>> tags = new ArrayList<>();

		private Enchantment cache;

		Builder(ResourceLocation id) {
			this.id = id;
			supported = primary = new HolderSetBuilder.Simple<>(Tags.Items.ENCHANTABLES);
			min = max = new Enchantment.Cost(10, 5);
			group = null;
		}

		public Builder transform(UnaryOperator<Builder> func) {
			return func.apply(this);
		}

		public Builder items(TagKey<Item> items) {
			return items(items, items);
		}

		public Builder items(TagKey<Item> supported, TagKey<Item> primary) {
			this.supported = new HolderSetBuilder.Simple<>(supported);
			this.primary = new HolderSetBuilder.Simple<>(primary);
			return this;
		}

		public Builder items(HolderSetBuilder<Item> items) {
			return items(items, items);
		}

		public Builder items(HolderSetBuilder<Item> supported, HolderSetBuilder<Item> primary) {
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
			this.exclude = HolderSetBuilder.tag(tag);
			return this;
		}

		public Builder exclusive(ResourceKey<Enchantment> key) {
			this.exclude = HolderSetBuilder.direct(key);
			return this;
		}

		public Builder exclusive(HolderSetBuilder<Enchantment> set) {
			this.exclude = set;
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

		public <T> Builder special(EECVal.Special<T> type, T val) {
			this.effects.add(e -> e.withSpecialEffect(type.get(), List.of(val)));
			return this;
		}

		public Builder color(EnchColor color) {
			return special(L2LibReg.COLOR, color);
		}

		Enchantment build(BootstrapContext<Enchantment> ctx, ResourceLocation id) {
			var items = ctx.registryLookup(Registries.ITEM).orElseThrow();
			var enchs = ctx.lookup(Registries.ENCHANTMENT);
			var fakeItem = new FakeRegistryLookup<>(Registries.ITEM);
			var fakeEnch = new FakeRegistryLookup<>(Registries.ENCHANTMENT);
			var ans = Enchantment.enchantment(new Enchantment.EnchantmentDefinition(
					supported.build(fakeItem, items), Optional.of(primary.build(fakeItem, items)),
					weight, maxLevel, min, max, anvilCost,
					group == null ? List.of() : List.of(group)));
			if (exclude != null) ans.exclusiveWith(exclude.build(fakeEnch, enchs));
			for (var e : effects) e.accept(ans);
			cache = ans.build(id);
			return cache;
		}

	}

}

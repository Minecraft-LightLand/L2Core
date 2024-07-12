package dev.xkmc.l2core.init.reg.ench;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.registries.holdersets.AndHolderSet;
import net.neoforged.neoforge.registries.holdersets.AnyHolderSet;
import net.neoforged.neoforge.registries.holdersets.NotHolderSet;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;

import java.util.List;

public interface HolderSetBuilder<T> {

	static <T> HolderSetBuilder<T> tag(TagKey<T> tag) {
		return new Simple<>(tag);
	}

	static <T> HolderSetBuilder<T> direct(ResourceKey<T> tag) {
		return new Direct<>(tag);
	}

	static <T> HolderSetBuilder<T> or(List<HolderSetBuilder<T>> tag) {
		return new Or<>(tag);
	}

	static <T> HolderSetBuilder<T> and(List<HolderSetBuilder<T>> tag) {
		return new And<>(tag);
	}

	static <T> HolderSetBuilder<T> not(HolderSetBuilder<T> tag) {
		return new Not<>(tag);
	}

	static <T> HolderSetBuilder<T> any() {
		return new Any<>();
	}

	HolderSet<T> build(HolderLookup.RegistryLookup<T> pvd);

	record Simple<T>(TagKey<T> tag) implements HolderSetBuilder<T> {

		@Override
		public HolderSet<T> build(HolderLookup.RegistryLookup<T> pvd) {
			return pvd.getOrThrow(tag);
		}

	}

	record Direct<T>(ResourceKey<T> key) implements HolderSetBuilder<T> {

		@Override
		public HolderSet<T> build(HolderLookup.RegistryLookup<T> pvd) {
			return HolderSet.direct(pvd.getOrThrow(key));
		}

	}

	record Or<T>(List<HolderSetBuilder<T>> list) implements HolderSetBuilder<T> {

		@Override
		public HolderSet<T> build(HolderLookup.RegistryLookup<T> pvd) {
			return new OrHolderSet<>(list.stream().map(e -> e.build(pvd)).toList());
		}

	}

	record And<T>(List<HolderSetBuilder<T>> list) implements HolderSetBuilder<T> {

		@Override
		public HolderSet<T> build(HolderLookup.RegistryLookup<T> pvd) {
			return new AndHolderSet<>(list.stream().map(e -> e.build(pvd)).toList());
		}

	}

	record Not<T>(HolderSetBuilder<T> val) implements HolderSetBuilder<T> {

		@Override
		public HolderSet<T> build(HolderLookup.RegistryLookup<T> pvd) {
			return new NotHolderSet<>(pvd, val.build(pvd));
		}

	}

	record Any<T>() implements HolderSetBuilder<T> {

		@Override
		public HolderSet<T> build(HolderLookup.RegistryLookup<T> pvd) {
			return new AnyHolderSet<>(pvd);
		}

	}

}

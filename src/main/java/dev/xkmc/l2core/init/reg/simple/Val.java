package dev.xkmc.l2core.init.reg.simple;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public interface Val<T> extends Supplier<T> {

	T get();

	ResourceLocation id();

	DeferredHolder<? super T, T> val();

	default ResourceKey<? super T> key() {
		return val().getKey();
	}

	record Registrate<H, T extends H>(RegistryEntry<H, T> val) implements Val<T> {

		@Override
		public T get() {
			return val.get();
		}

		@Override
		public ResourceLocation id() {
			return val.getId();
		}

	}

}

package dev.xkmc.l2core.init.reg.simple;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface Val<T> extends Supplier<T> {

	T get();

	ResourceLocation id();

	record Registrate<H, T extends H>(RegistryEntry<H, T> entry) implements Val<T> {

		@Override
		public T get() {
			return entry.get();
		}

		@Override
		public ResourceLocation id() {
			return entry.getId();
		}

	}

}

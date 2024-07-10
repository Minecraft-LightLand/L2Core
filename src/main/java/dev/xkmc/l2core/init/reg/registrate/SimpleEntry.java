package dev.xkmc.l2core.init.reg.registrate;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public record SimpleEntry<T>(RegistryEntry<T, ? extends T> val) implements Supplier<T> {

	public ResourceKey<T> key() {
		return val.getKey();
	}

	public T get() {
		return val.get();
	}

	public Holder<T> holder() {
		return val.getDelegate();
	}

}

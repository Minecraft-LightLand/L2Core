package dev.xkmc.l2core.init.reg.registrate;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.resources.ResourceKey;

public record SimpleEntry<T>(RegistryEntry<T, ? extends T> val) {

	ResourceKey<T> key() {
		return val.getKey();
	}

	T get() {
		return val.get();
	}

}

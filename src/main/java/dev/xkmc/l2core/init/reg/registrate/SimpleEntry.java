package dev.xkmc.l2core.init.reg.registrate;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

public record SimpleEntry<T>(RegistryEntry<T, ? extends T> val) implements LegacyHolder<T> {

	public ResourceKey<T> key() {
		return val.getKey();
	}

	public T get() {
		return val.get();
	}

	@Override
	public Holder<T> holder() {
		return val;
	}

	@Override
	public int hashCode() {
		return key().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		return obj instanceof Holder<?> h &&
				h.kind() == Kind.REFERENCE &&
				key().equals(h.getKey());
	}

}

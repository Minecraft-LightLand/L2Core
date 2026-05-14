package dev.xkmc.l2core.init.reg.simple;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public record SR<T>(DeferredRegister<T> reg) {

	public static <T> SR<T> of(Reg parent, Registry<T> reg) {
		return new SR<>(parent.make(reg));
	}

	public static <T> SR<T> of(Reg parent, ResourceKey<Registry<T>> reg) {
		return new SR<>(parent.make(reg));
	}

	public <H extends T> ValImpl<T, H> reg(String id, Supplier<H> sup) {
		return new ValImpl<>(reg.register(id, sup), Optional.empty());
	}

	public <H extends T> ValImpl<T, H> regVal(String id, H val) {
		return new ValImpl<>(reg.register(id, () -> val), Optional.of(val));
	}

	public <H extends T> ValImpl<T, H> reg(String id, Function<Identifier, H> sup) {
		return new ValImpl<>(reg.register(id, sup), Optional.empty());
	}

	public record ValImpl<R, T extends R>(DeferredHolder<R, T> val, Optional<T> v) implements Val<T> {

		@Override
		public T get() {
			return v.orElseGet(val);
		}

		@Override
		public Identifier id() {
			return val.getId();
		}

	}

}

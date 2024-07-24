package dev.xkmc.l2core.init.reg.simple;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

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
		return new ValImpl<>(reg.register(id, sup));
	}

	public <H extends T> ValImpl<T, H> reg(String id, Function<ResourceLocation, H> sup) {
		return new ValImpl<>(reg.register(id, sup));
	}

	public record ValImpl<R, T extends R>(DeferredHolder<R, T> val) implements Val<T> {

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

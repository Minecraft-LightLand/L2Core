package dev.xkmc.l2core.init.reg.simple;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2serial.serialization.codec.MapCodecAdaptor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public record CdcReg<T>(DeferredRegister<MapCodec<? extends T>> reg) {

	public static <T> CdcReg<T> of(Reg parent, Registry<MapCodec<? extends T>> reg) {
		return new CdcReg<>(parent.make(reg));
	}

	public <R extends T> CdcVal<R> reg(String id, MapCodec<R> codec) {
		return new CdcValImpl<>(reg.register(id, () -> codec));
	}

	public <R extends T> CdcVal<R> reg(String id, Class<R> cls) {
		return new CdcValImpl<>(reg.register(id, () -> MapCodecAdaptor.of(cls)));
	}

	private record CdcValImpl<R extends T, T>(DeferredHolder<MapCodec<? extends T>, MapCodec<R>> val)
			implements CdcVal<R> {

		@Override
		public MapCodec<R> get() {
			return val.get();
		}

		@Override
		public ResourceLocation id() {
			return val.getId();
		}

	}

}

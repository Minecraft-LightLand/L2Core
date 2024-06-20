package dev.xkmc.l2core.init.reg.simple;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2serial.serialization.codec.MapCodecAdaptor;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public record IngReg(DeferredRegister<IngredientType<?>> reg) {

	public static IngReg of(Reg reg) {
		return new IngReg(reg.make(NeoForgeRegistries.INGREDIENT_TYPES));
	}

	public <R extends ICustomIngredient> IngVal<R> reg(String id, MapCodec<R> codec) {
		return new IngValImpl<>(reg.register(id, () -> new IngredientType<>(codec)));
	}

	public <R extends ICustomIngredient> IngVal<R> reg(String id, Class<R> cls) {
		return new IngValImpl<>(reg.register(id, () -> new IngredientType<>(MapCodecAdaptor.of(cls))));
	}

	private record IngValImpl<R extends ICustomIngredient>(DeferredHolder<IngredientType<?>, IngredientType<R>> val)
			implements IngVal<R> {

		@Override
		public IngredientType<R> get() {
			return val.get();
		}

	}

}

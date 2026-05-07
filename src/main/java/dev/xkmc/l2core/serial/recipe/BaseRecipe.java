package dev.xkmc.l2core.serial.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

public abstract class BaseRecipe<Rec extends SRec, SRec extends BaseRecipe<?, SRec, Inv>, Inv extends RecipeInput> implements Recipe<Inv> {

	private final RecType<Rec, SRec, Inv> factory;

	public BaseRecipe(RecType<Rec, SRec, Inv> fac) {
		factory = fac;
	}

	@Override
	public final RecipeSerializer<? extends Recipe<Inv>> getSerializer() {
		return factory.serializer();
	}

	@Override
	public final RecipeType<? extends Recipe<Inv>> getType() {
		return factory.type.get();
	}

	public static class RecType<Rec extends SRec, SRec extends BaseRecipe<?, SRec, Inv>, Inv extends RecipeInput> extends RecSerializer<Rec, Inv> {

		public final Supplier<RecipeType<SRec>> type;

		public RecType(Class<Rec> rec, Supplier<RecipeType<SRec>> type) {
			super(rec);
			this.type = type;
		}

	}

}
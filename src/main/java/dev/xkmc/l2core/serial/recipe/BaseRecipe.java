package dev.xkmc.l2core.serial.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public abstract class BaseRecipe<Rec extends SRec, SRec extends BaseRecipe<?, SRec, Inv>, Inv extends RecipeInput> implements Recipe<Inv> {

	private final RecType<Rec, SRec, Inv> factory;

	public BaseRecipe(RecType<Rec, SRec, Inv> fac) {
		factory = fac;
	}

	@Override
	public abstract boolean matches(Inv inv, Level world);

	@Override
	public abstract ItemStack assemble(Inv inv, HolderLookup.Provider provider);

	@Override
	public abstract ItemStack getResultItem(HolderLookup.Provider provider);

	@Override
	public abstract boolean canCraftInDimensions(int r, int c);

	@Override
	public final RecipeSerializer<?> getSerializer() {
		return factory;
	}

	@Override
	public final RecipeType<?> getType() {
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
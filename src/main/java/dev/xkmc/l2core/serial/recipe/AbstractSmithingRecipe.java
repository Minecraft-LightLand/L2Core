package dev.xkmc.l2core.serial.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractSmithingRecipe<T extends AbstractSmithingRecipe<T>> extends SmithingTransformRecipe {

	public static final Ingredient TEMPLATE_PLACEHOLDER = Ingredient.EMPTY;

	public AbstractSmithingRecipe(Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
		super(template, base, addition, result);
	}

	@Override
	public abstract Serializer<T> getSerializer();

	@FunctionalInterface
	public interface RecipeFactory<T extends AbstractSmithingRecipe<T>> {

		T create(Ingredient template, Ingredient base, Ingredient addition, ItemStack result);

		default T map(SmithingTransformRecipe r) {
			return create(r.template, r.base, r.addition, r.result);
		}

	}

	public static class Serializer<T extends AbstractSmithingRecipe<T>> extends SmithingTransformRecipe.Serializer {

		private final RecipeFactory<T> factory;

		public Serializer(RecipeFactory<T> factory) {
			this.factory = factory;
		}

		@Override
		public MapCodec<SmithingTransformRecipe> codec() {
			return super.codec().xmap(factory::map, e -> e);
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SmithingTransformRecipe> streamCodec() {
			return super.streamCodec().map(factory::map, e -> e);
		}

	}

}
package dev.xkmc.l2core.serial.recipe;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractSmithingRecipe<T extends AbstractSmithingRecipe<T>> extends SmithingTransformRecipe {

	public static <T extends AbstractSmithingRecipe<T>> RecipeSerializer<T> serializer(RecipeFactory<T> fac) {
		return new RecipeSerializer<>(MAP_CODEC.xmap(fac::map, r -> r), STREAM_CODEC.map(fac::map, r -> r));
	}

	public AbstractSmithingRecipe(CommonInfo commonInfo, Optional<Ingredient> template, Ingredient base, Optional<Ingredient> addition, ItemStackTemplate result) {
		super(commonInfo, template, base, addition, result);
	}

	@FunctionalInterface
	public interface RecipeFactory<T extends AbstractSmithingRecipe<T>> {

		T create(CommonInfo commonInfo, Optional<Ingredient> template, Ingredient base, Optional<Ingredient> addition, ItemStackTemplate result);

		default T map(SmithingTransformRecipe r) {
			return create(new CommonInfo(r.showNotification()), r.template, r.base, r.addition, r.result);
		}

	}

}
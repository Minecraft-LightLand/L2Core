package dev.xkmc.l2core.serial.recipe;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractShapelessRecipe<T extends AbstractShapelessRecipe<T>> extends ShapelessRecipe {

	public static <T extends AbstractShapelessRecipe<T>> RecipeSerializer<T> serializer(RecipeFactory<T> fac) {
		return new RecipeSerializer<>(MAP_CODEC.xmap(fac::map, r -> r), STREAM_CODEC.map(fac::map, r -> r));
	}

	public AbstractShapelessRecipe(CommonInfo commonInfo, CraftingBookInfo bookInfo, ItemStackTemplate result, List<Ingredient> ingredients) {
		super(commonInfo, bookInfo, result, ingredients);
	}

	public List<ItemStackTemplate> getJEIResult() {
		return List.of(result);
	}

	@FunctionalInterface
	public interface RecipeFactory<T extends AbstractShapelessRecipe<T>> {

		T create(CommonInfo commonInfo, CraftingBookInfo bookInfo, ItemStackTemplate result, List<Ingredient> ingredients);

		default T map(ShapelessRecipe r) {
			return create(new CommonInfo(r.showNotification()), new CraftingBookInfo(r.category(), r.group()), r.result, r.ingredients);
		}

	}

}


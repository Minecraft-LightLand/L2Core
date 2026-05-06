package dev.xkmc.l2core.serial.recipe;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractShapedRecipe<T extends AbstractShapedRecipe<T>> extends ShapedRecipe {

	public AbstractShapedRecipe(CommonInfo commonInfo, CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result) {
		super(commonInfo, bookInfo, pattern, result);
	}

	@FunctionalInterface
	public interface RecipeFactory<T extends AbstractShapedRecipe<T>> {

		T create(CommonInfo commonInfo, CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result);

		default T map(ShapedRecipe r) {
			return create(new CommonInfo(r.showNotification()), new CraftingBookInfo(r.category(), r.group()), r.pattern, r.result);
		}

	}

}
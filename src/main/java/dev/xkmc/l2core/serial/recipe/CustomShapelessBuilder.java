package dev.xkmc.l2core.serial.recipe;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;

public class CustomShapelessBuilder<T extends AbstractShapelessRecipe<T>> extends ShapelessRecipeBuilder {

	private final AbstractShapelessRecipe.RecipeFactory<T> factory;

	public CustomShapelessBuilder(AbstractShapelessRecipe.RecipeFactory<T> factory, HolderGetter<Item> items, RecipeCategory category, ItemStackTemplate result) {
		super(items, category, result);
		this.factory = factory;
	}

	@Override
	public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
		super.save(new DelegateRecipeOutput<>(output, factory::map), id);
	}

}

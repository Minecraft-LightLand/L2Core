package dev.xkmc.l2core.serial.recipe;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class CustomSmithingBuilder<T extends AbstractSmithingRecipe<T>> extends SmithingTransformRecipeBuilder {

	private final AbstractSmithingRecipe.RecipeFactory<T> factory;

	public CustomSmithingBuilder(AbstractSmithingRecipe.RecipeFactory<T> factory, Ingredient template, Ingredient base, Ingredient addition, RecipeCategory category, ItemStackTemplate result) {
		super(template, base, addition, category, result);
		this.factory = factory;
	}

	public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
		super.save(new DelegateRecipeOutput<>(output, factory::map), id);
	}

}

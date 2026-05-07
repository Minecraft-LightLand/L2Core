package dev.xkmc.l2core.serial.recipe;

import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public record DelegateRecipeOutput<T extends Recipe<?>, R extends Recipe<?>>(
		RecipeOutput output, Function<T, R> mapper) implements RecipeOutput {

	@Override
	public Advancement.Builder advancement() {
		return output.advancement();
	}

	@Override
	public void includeRootAdvancement() {
		output.includeRootAdvancement();
	}

	@Override
	public void accept(ResourceKey<Recipe<?>> key, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
		output.accept(key, mapper.apply(Wrappers.cast(recipe)), advancement, conditions);
	}

}

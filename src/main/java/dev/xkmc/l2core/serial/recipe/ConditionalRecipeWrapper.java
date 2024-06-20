package dev.xkmc.l2core.serial.recipe;

import dev.xkmc.l2core.util.MathHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public record ConditionalRecipeWrapper(RecipeOutput pvd, ICondition... conditions) implements RecipeOutput {

	@Override
	public Advancement.Builder advancement() {
		return pvd.advancement();
	}

	@Override
	public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
		pvd.accept(id, recipe, advancement, MathHelper.merge(conditions(), conditions));
	}

}

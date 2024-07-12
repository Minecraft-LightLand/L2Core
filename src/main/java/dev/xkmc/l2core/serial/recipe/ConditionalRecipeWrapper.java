package dev.xkmc.l2core.serial.recipe;

import dev.xkmc.l2core.util.MathHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import org.jetbrains.annotations.Nullable;

public record ConditionalRecipeWrapper(RecipeOutput pvd, ICondition... conditions) implements RecipeOutput {

	public static RecipeOutput mod(RecipeOutput pvd, String... modid) {
		ICondition[] ans = new ICondition[modid.length];
		for (int i = 0; i < ans.length; ++i) {
			ans[i] = new ModLoadedCondition(modid[i]);
		}
		return new ConditionalRecipeWrapper(pvd, ans);

	}

	public static RecipeOutput of(RecipeOutput pvd, ICondition... cond) {
		return new ConditionalRecipeWrapper(pvd, cond);
	}

	@Override
	public Advancement.Builder advancement() {
		return pvd.advancement();
	}

	@Override
	public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
		pvd.accept(id, recipe, advancement, MathHelper.merge(conditions(), conditions));
	}

}

package dev.xkmc.l2core.serial.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.Nullable;

public class BaseRecipeBuilder<
		T extends BaseRecipeBuilder<T, Rec, SRec, Inv>,
		Rec extends SRec,
		SRec extends BaseRecipe<?, SRec, Inv>,
		Inv extends RecipeInput
		> implements RecipeBuilder {

	protected final BaseRecipe.RecType<Rec, SRec, Inv> type;
	protected final Rec recipe;
	protected final ItemStackTemplate result;
	protected final RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();

	public BaseRecipeBuilder(BaseRecipe.RecType<Rec, SRec, Inv> type, ItemStackTemplate result) {
		this.type = type;
		this.recipe = type.blank();
		this.result = result;
	}

	@SuppressWarnings({"unchecked", "unsafe"})
	public T getThis() {
		return (T) this;
	}

	@Override
	public T unlockedBy(String name, Criterion<?> trigger) {
		advancementBuilder.unlockedBy(name, trigger);
		return getThis();
	}

	@Override
	public T group(@Nullable String pGroupName) {
		return getThis();
	}

	@Override
	public ResourceKey<Recipe<?>> defaultId() {
		return RecipeBuilder.getDefaultRecipeId(this.result);
	}

	@Override
	public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
		output.accept(id, recipe, advancementBuilder.build(output, id, RecipeCategory.MISC));
	}

}


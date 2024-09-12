package dev.xkmc.l2core.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BaseRecipeCategory<T, C extends BaseRecipeCategory<T, C>> implements IRecipeCategory<T> {

	@SuppressWarnings("unchecked")
	public static <T extends R, R> Class<T> cast(Class<R> cls) {
		return (Class<T>) cls;
	}

	private final RecipeType<T> type;

	protected IDrawable background, icon;

	public BaseRecipeCategory(ResourceLocation name, Class<T> cls) {
		this.type = new RecipeType<>(name, cls);
	}

	public <R extends Recipe<I>, I extends RecipeInput> List<R> getAll(net.minecraft.world.item.crafting.RecipeType<R> type) {
		var level = Minecraft.getInstance().level;
		if (level == null) return List.of();
		return level.getRecipeManager().getAllRecipesFor(type)
				.stream().map(RecipeHolder::value).toList();
	}

	@SuppressWarnings("unchecked")
	public final C getThis() {
		return (C) this;
	}

	@Override
	public final RecipeType<T> getRecipeType() {
		return type;
	}

	@Override
	public final IDrawable getBackground() {
		return background;
	}

	@Override
	public final IDrawable getIcon() {
		return icon;
	}

	@Override
	public abstract void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses);

}

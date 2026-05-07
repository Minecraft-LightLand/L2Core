package dev.xkmc.l2core.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.*;

import java.util.List;

public abstract class BaseRecipeCategory<T, C extends BaseRecipeCategory<T, C>> implements IRecipeCategory<T> {

	@SuppressWarnings("unchecked")
	public static <T extends R, R> Class<T> cast(Class<R> cls) {
		return (Class<T>) cls;
	}

	private final IRecipeType<T> type;

	protected IDrawable icon;

	public BaseRecipeCategory(Identifier name, Class<T> cls) {
		this.type = IRecipeType.create(name, cls);
	}

	public <R extends Recipe<I>, I extends RecipeInput> List<R> getAll(RecipeMap map, RecipeType<R> type) {
		var level = Minecraft.getInstance().level;
		if (level == null) return List.of();
		return map.byType(type).stream().map(RecipeHolder::value).toList();
	}

	@SuppressWarnings("unchecked")
	public final C getThis() {
		return (C) this;
	}

	@Override
	public final IRecipeType<T> getRecipeType() {
		return type;
	}

	@Override
	public final IDrawable getIcon() {
		return icon;
	}

	@Override
	public abstract void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses);

}

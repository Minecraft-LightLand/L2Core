package dev.xkmc.l2core.init.reg.simple;

import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public interface IngVal<T extends ICustomIngredient> extends Val<IngredientType<T>> {

}

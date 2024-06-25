package dev.xkmc.l2core.init.reg.registrate;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public record RecipeTypeEntry<T extends Recipe<?>>(RegistryEntry<RecipeType<?>, RecipeType<T>> val) {

	ResourceKey<RecipeType<?>> key() {
		return val.getKey();
	}

	RecipeType<T> get() {
		return val.get();
	}

}

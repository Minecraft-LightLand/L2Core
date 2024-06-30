package dev.xkmc.l2core.init.reg.simple;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface DCVal<T> extends Val<DataComponentType<T>> {

	@Nullable
	default T get(ItemStack stack) {
		return stack.get(this);
	}

	default T getOrDefault(ItemStack stack, T def) {
		T ans = stack.get(this);
		return ans == null ? def : ans;
	}

	default ItemStack set(ItemStack stack, T val) {
		stack.set(this, val);
		return stack;
	}

}

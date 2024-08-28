package dev.xkmc.l2core.init.reg.varitem;

import com.tterrag.registrate.builders.ItemBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public interface VarBuilder<T extends Item> {

	static <T extends Item> VarBuilder<T> nop() {
		return (r, b) -> b;
	}

	ItemBuilder<T, ?> buildImpl(ResourceLocation id, ItemBuilder<T, ?> builder);

	default <R> ItemBuilder<T, R> build(ResourceLocation id, ItemBuilder<T, R> builder) {
		buildImpl(id, builder);
		return builder;
	}

}

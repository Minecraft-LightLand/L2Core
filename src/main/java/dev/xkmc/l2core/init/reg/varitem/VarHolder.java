package dev.xkmc.l2core.init.reg.varitem;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class VarHolder<T extends Item> implements VarEntry<T>, ItemLike {

	private final String str;
	private final VarBuilder<T> builder;
	private ItemEntry<T> item;

	public VarHolder(String str, VarBuilder<T> builder) {
		this.str = str;
		this.builder = builder;
	}

	public ItemEntry<T> item() {
		return item;
	}

	@Override
	public Item asItem() {
		return item.asItem();
	}

	@Override
	public String id() {
		return str;
	}

	@Override
	public void callback(ItemEntry<T> item) {
		this.item = item;
	}

	@Override
	public VarBuilder<T> builder() {
		return builder;
	}

}

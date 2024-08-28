package dev.xkmc.l2core.init.reg.varitem;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Contract;
import org.openjdk.nashorn.internal.objects.annotations.Getter;

import javax.annotation.Nullable;

public interface VarEntry<T extends Item> {

	String id();

	void callback(ItemEntry<T> item);

	VarBuilder<T> builder();

}

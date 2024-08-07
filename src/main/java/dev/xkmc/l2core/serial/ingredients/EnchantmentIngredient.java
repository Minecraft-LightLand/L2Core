package dev.xkmc.l2core.serial.ingredients;

import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.util.DataGenOnly;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public record EnchantmentIngredient(Holder<Enchantment> enchantment, int minLevel) implements ICustomIngredient {

	public static Ingredient of(Holder<Enchantment> ench, int min) {
		return new EnchantmentIngredient(ench, min).toVanilla();
	}

	@DataGenOnly
	public static Ingredient of(HolderLookup.Provider pvd, ResourceKey<Enchantment> ench, int min) {
		var holder = pvd.lookup(Registries.ENCHANTMENT).orElseThrow().getOrThrow(ench);
		return of(holder, min);
	}

	@Override
	public Stream<ItemStack> getItems() {
		var ench = enchantment.value();
		return IntStream.range(minLevel, ench.getMaxLevel() + 1)
				.mapToObj(i -> EnchantedBookItem.createForEnchantment(
						new EnchantmentInstance(enchantment, i)));
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IngredientType<?> getType() {
		return L2LibReg.ING_ENCH.get();
	}

	public boolean test(ItemStack stack) {
		return EnchantmentHelper.getEnchantmentsForCrafting(stack).getLevel(enchantment()) >= minLevel();
	}

}

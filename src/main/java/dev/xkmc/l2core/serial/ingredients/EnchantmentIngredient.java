package dev.xkmc.l2core.serial.ingredients;

import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.util.DataGenOnly;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.ArrayList;
import java.util.List;
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
	public SlotDisplay display() {
		List<SlotDisplay> ans = new ArrayList<>();
		var ench = enchantment.value();
		for (int i = minLevel; i <= ench.getMaxLevel(); i++) {
			var map = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
			map.set(enchantment, i);
			var patch = DataComponentPatch.builder()
					.set(DataComponents.ENCHANTMENTS, map.toImmutable()).build();
			var stack = new ItemStackTemplate(Items.ENCHANTED_BOOK, patch);
			ans.add(new SlotDisplay.ItemStackSlotDisplay(stack));
		}
		return new SlotDisplay.Composite(ans);
	}

	@Override
	public Stream<Holder<Item>> items() {
		return Stream.of(Items.ENCHANTED_BOOK.builtInRegistryHolder());
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

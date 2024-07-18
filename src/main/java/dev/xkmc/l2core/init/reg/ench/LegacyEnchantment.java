package dev.xkmc.l2core.init.reg.ench;

import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LegacyEnchantment {

	@Nullable
	public static <T> T firstOf(Holder<Enchantment> ench, Class<T> cls) {
		for (var e : ench.value().getEffects(L2LibReg.LEGACY.get())) {
			if (cls.isInstance(e)) {
				return Wrappers.cast(e);
			}
		}
		return null;
	}

	public static <T> Optional<T> findFirst(Holder<Enchantment> ench, Class<T> cls) {
		return Optional.ofNullable(firstOf(ench, cls));
	}

	public static <T> List<EnchHolder<T>> findAll(ItemStack stack, Class<T> cls, boolean intrinsic) {
		var reg = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
		if (reg == null) return List.of();
		List<EnchHolder<T>> ans = new ArrayList<>();
		ItemEnchantments enchs = intrinsic ?
				stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY) :
				stack.getAllEnchantments(reg);
		for (var e : enchs.entrySet()) {
			var t = firstOf(e.getKey(), cls);
			if (t != null) ans.add(new EnchHolder<>(e.getKey(), t, e.getIntValue()));
		}
		return ans;
	}

	public static <T> Map<T, Integer> accumulateOnEntity(LivingEntity le, Class<T> cls, boolean intrinsic) {
		LinkedHashMap<T, Integer> map = new LinkedHashMap<>();
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack stack = le.getItemBySlot(slot);
			if (stack.isEmpty() || !stack.isEnchanted()) continue;
			var list = findAll(stack, cls, intrinsic);
			for (var e : list) {
				if (e.holder().value().matchingSlot(slot)) {
					map.compute(e.val(), (k, v) -> (v == null ? 0 : v) + e.lv());
				}
			}
		}
		return map;
	}

	public List<Component> descFull(int lv, String key, boolean alt, boolean book) {
		return List.of(Component.translatable(key).withStyle(ChatFormatting.DARK_GRAY));
	}

}

package dev.xkmc.l2core.init.reg.ench;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;

public record EnchHolder<T>(Holder<Enchantment> holder, T val, int lv) {
}

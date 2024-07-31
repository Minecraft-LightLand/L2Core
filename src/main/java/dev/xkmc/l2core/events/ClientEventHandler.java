package dev.xkmc.l2core.events;

import com.mojang.datafixers.util.Either;
import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2core.init.L2CoreConfig;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.init.reg.ench.CustomDescEnchantment;
import dev.xkmc.l2core.init.reg.ench.EnchColor;
import dev.xkmc.l2core.init.reg.ench.LegacyEnchantment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@EventBusSubscriber(value = Dist.CLIENT, modid = L2Core.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ClientEventHandler {

	public enum EnchDesc {
		DISABLE, SHIFT_ONLY, ALWAYS
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void modifyItemTooltip(ItemTooltipEvent event) {
		var config = L2CoreConfig.CLIENT.addEnchantmentDescription.get();
		boolean skip = config == EnchDesc.DISABLE || config == EnchDesc.SHIFT_ONLY && !Screen.hasShiftDown();
		var list = event.getToolTip();
		int n = list.size();
		ItemStack stack = event.getItemStack();
		if (!stack.isEnchanted() && !stack.is(Items.ENCHANTED_BOOK)) return;
		var map = EnchantmentHelper.getEnchantmentsForCrafting(stack);
		String prefix = "enchantment.";
		String suffix = ".desc";
		boolean alt = Screen.hasAltDown();
		boolean flag = false;
		boolean book = event.getItemStack().is(Items.ENCHANTED_BOOK);
		var registries = event.getContext().registries();
		if (registries == null) return;
		var reg = registries.lookup(Registries.ENCHANTMENT);
		if (reg.isEmpty()) return;
		List<Either<Component, List<Component>>> compound = new ArrayList<>();
		for (var e : list) {
			compound.add(Either.left(e));
		}
		for (int i = 0; i < n; i++) {
			Component comp = list.get(i);
			Component lit;
			if (comp.getContents() instanceof PlainTextContents.LiteralContents txt && comp.getSiblings().size() == 1) {
				comp = comp.getSiblings().getFirst();
				lit = Component.literal(txt.text());
			} else lit = Component.empty();
			if (!(comp.getContents() instanceof TranslatableContents tr)) continue;
			if (!tr.getKey().startsWith(prefix)) continue;
			if (tr.getKey().endsWith(suffix)) {
				compound.set(i, Either.right(List.of()));
				flag = true;
				continue;
			}
			String id = tr.getKey().substring(prefix.length());
			var rl = ResourceLocation.tryBySeparator(id, '.');
			if (rl == null) continue;
			var ench = reg.get().get(ResourceKey.create(Registries.ENCHANTMENT, rl));
			if (ench.isEmpty()) continue;
			var color = LegacyEnchantment.firstOf(ench.get(), L2LibReg.COLOR);
			if (color != null) {
				comp = comp.copy().withStyle(color.base());
				compound.set(i, Either.left(comp));
				flag = true;
			} else color = EnchColor.DEFAULT;

			var legacy = LegacyEnchantment.findFirst(ench.get(), CustomDescEnchantment.class);
			if (legacy.isPresent()){
				comp = legacy.get().title(stack, list.get(i), alt, book, color);
				compound.set(i, Either.left(comp));
				flag = true;
			}
			if (skip) continue;
			if (legacy.isEmpty()) {
				if (I18n.exists(tr.getKey() + ".desc")) {
					compound.set(i, Either.right(List.of(comp, Component.translatable(tr.getKey() + ".desc")
							.withStyle(color.desc()))));
					flag = true;
				}
			} else {
				int lv = map.getLevel(ench.get());
				var es = legacy.get().descFull(stack, lv, tr.getKey() + ".desc", alt, book, color);
				compound.set(i, Either.right(Stream.concat(Stream.of(comp), es.stream().map(e -> (Component) lit.copy().append(e))).toList()));
				flag = true;
			}
		}
		if (flag) {
			list.clear();
			list.addAll(compound.stream().flatMap(e -> e.map(Stream::of, Collection::stream)).toList());
		}
	}

}

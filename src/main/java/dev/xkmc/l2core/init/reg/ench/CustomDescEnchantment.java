package dev.xkmc.l2core.init.reg.ench;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;

import java.util.List;

public interface CustomDescEnchantment {

	default List<Component> descFull(int lv, String key, boolean alt, boolean book, EnchColor color) {
		return List.of(Component.translatable(key).withStyle(color.desc()));
	}

	static MutableComponent perc(double val) {
		return Component.literal(Math.round(val * 100) + "%").withStyle(ChatFormatting.DARK_AQUA);
	}

	static MutableComponent perc(int lv, double val, boolean alt) {
		return (alt ? Component.literal("[Lv]x" + (int) Math.round(val * 100) + "%") :
				Component.literal((int) Math.round(lv * val * 100) + "%"))
				.withStyle(ChatFormatting.DARK_AQUA);
	}

	static MutableComponent percSmall(int lv, double val, boolean alt) {
		return (alt ? Component.literal("[Lv]x" + (int) Math.round(val * 10000) / 100d + "%") :
				Component.literal((int) Math.round(lv * val * 10000) / 100d + "%"))
				.withStyle(ChatFormatting.DARK_AQUA);
	}

	static MutableComponent num(int val) {
		return Component.literal("" + val).withStyle(ChatFormatting.DARK_AQUA);
	}

	static MutableComponent num(int lv, int val, boolean alt) {
		return (alt ? val == 1 ? Component.literal("[Lv]") : Component.literal("[Lv]x" + val) :
				Component.literal(lv * val + ""))
				.withStyle(ChatFormatting.DARK_AQUA);
	}

	static MutableComponent numSmall(double val) {
		return Component.literal("" + val).withStyle(ChatFormatting.DARK_AQUA);
	}

	static MutableComponent numSmall(int lv, double val, boolean alt) {
		return (alt ? val == 1 ? Component.literal("[Lv]") : Component.literal("[Lv]x" + val) :
				Component.literal(lv * val + ""))
				.withStyle(ChatFormatting.DARK_AQUA);
	}

	static Component ench(String key, MutableComponent... perc) {
		return Component.translatable(key, (Object[]) perc).withStyle(ChatFormatting.GRAY);
	}

	static MutableComponent eff(MobEffectInstance ins) {
		return eff(ins, true, true);
	}

	static MutableComponent eff(MobEffectInstance ins, boolean showLevel, boolean showDuration) {
		MutableComponent desc = Component.translatable(ins.getDescriptionId());
		if (showLevel && ins.getAmplifier() > 0) {
			desc = Component.translatable("potion.withAmplifier", desc,
					Component.translatable("potion.potency." + ins.getAmplifier()));
		}
		if (showDuration && !ins.endsWithin(19)) {
			desc = Component.translatable("potion.withDuration", desc, MobEffectUtil.formatDuration(ins, 1, 20));
		}
		return desc.withStyle(ins.getEffect().value().getCategory().getTooltipFormatting());
	}

	static MutableComponent eff(MobEffect eff) {
		return eff.getDisplayName().copy().withStyle(eff.getCategory().getTooltipFormatting());
	}

}

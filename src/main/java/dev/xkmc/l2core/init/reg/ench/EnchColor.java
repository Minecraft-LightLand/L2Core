package dev.xkmc.l2core.init.reg.ench;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;

public record EnchColor(ChatFormatting base, ChatFormatting desc) {

	public static final EnchColor DEFAULT = new EnchColor(ChatFormatting.GRAY, ChatFormatting.DARK_GRAY);

	public static final Codec<EnchColor> CODEC = RecordCodecBuilder.create(i -> i.group(
			ChatFormatting.CODEC.fieldOf("base").forGetter(e -> e.base),
			ChatFormatting.CODEC.fieldOf("desc").forGetter(e -> e.desc)
	).apply(i, EnchColor::new));

}

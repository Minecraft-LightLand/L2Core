package dev.xkmc.l2core.serial.advancements;

import com.mojang.serialization.Codec;
import dev.xkmc.l2serial.serialization.codec.CodecAdaptor;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;

public class BaseCriterion<T extends BaseCriterionInstance<T, R>, R extends BaseCriterion<T, R>> extends SimpleCriterionTrigger<T> {

	private final Codec<T> codec;

	public BaseCriterion(Class<T> cls) {
		this.codec = new CodecAdaptor<>(cls);
	}

	@Override
	public Codec<T> codec() {
		return codec;
	}

}

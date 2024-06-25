package dev.xkmc.l2core.init.reg.registrate;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;

public record EffectEntry<T extends MobEffect>(RegistryEntry<MobEffect, T> val) {

	ResourceKey<MobEffect> key() {
		return val.getKey();
	}

	T get() {
		return val.get();
	}

}

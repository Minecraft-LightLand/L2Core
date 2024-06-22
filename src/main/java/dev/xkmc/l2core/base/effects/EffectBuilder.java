package dev.xkmc.l2core.base.effects;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class EffectBuilder {

	public final MobEffectInstance ins;

	public EffectBuilder(MobEffectInstance ins) {
		this.ins = ins;
	}

	public EffectBuilder(Holder<MobEffect> effect) {
		this.ins = new MobEffectInstance(effect, 1, 0);
	}

	public EffectBuilder setAmplifier(int amplifier) {
		ins.amplifier = amplifier;
		return this;
	}

	public EffectBuilder setDuration(int duration) {
		ins.duration = duration;
		return this;
	}

	public EffectBuilder setVisible(boolean visible) {
		ins.visible = visible;
		return this;
	}

	public EffectBuilder setAmbient(boolean ambient) {
		ins.ambient = ambient;
		return this;
	}

	public EffectBuilder setShowIcon(boolean showIcon) {
		ins.showIcon = showIcon;
		return this;
	}

}

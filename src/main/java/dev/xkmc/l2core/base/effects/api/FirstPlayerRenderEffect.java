package dev.xkmc.l2core.base.effects.api;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.effect.MobEffectInstance;

public interface FirstPlayerRenderEffect {

	void onClientLevelRender(AbstractClientPlayer player, MobEffectInstance value);

}

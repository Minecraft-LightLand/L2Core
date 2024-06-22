package dev.xkmc.l2core.base.effects;

import dev.xkmc.l2core.events.ClientEffectRenderEvents;
import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public record EffectToClient(int entity, Holder<MobEffect> effect, boolean exist, int level)
		implements SerialPacketBase<EffectToClient> {

	@Override
	public void handle(@Nullable Player player) {
		ClientEffectRenderEvents.sync(this);
	}

}

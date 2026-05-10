package dev.xkmc.l2core.capability.conditionals;

import dev.xkmc.l2core.init.L2Core;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public interface NetworkSensitiveToken<T extends ConditionalToken> {

	void onSync(@Nullable T old, LivingEntity entity);

	default boolean broadcastOnTracking() {
		return false;
	}

	default void sync(TokenKey<T> key, T token, LivingEntity entity, ServerPlayer sp) {
		L2Core.PACKET_HANDLER.toClientPlayer(TokenToClient.of(sp.registryAccess(), key, entity, token), sp);
	}

	default void broadcast(TokenKey<T> key, T token, LivingEntity entity) {
		L2Core.PACKET_HANDLER.toTrackingPlayers(TokenToClient.of(entity.registryAccess(), key, entity, token), entity);
	}

}

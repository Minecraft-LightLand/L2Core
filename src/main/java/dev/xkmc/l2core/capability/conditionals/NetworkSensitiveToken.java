package dev.xkmc.l2core.capability.conditionals;

import dev.xkmc.l2core.init.L2Core;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface NetworkSensitiveToken<T extends ConditionalToken> {
	void onSync(@Nullable T old, Player player);

	default void sync(TokenKey<T> key, T token, ServerPlayer sp) {
		L2Core.PACKET_HANDLER.toClientPlayer(TokenToClient.of(key, token), sp);
	}

}

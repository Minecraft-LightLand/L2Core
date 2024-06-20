package dev.xkmc.l2core.capability.player;

import dev.xkmc.l2core.init.L2Core;
import net.minecraft.server.level.ServerPlayer;

public class PlayerCapabilityNetworkHandler<T extends PlayerCapabilityTemplate<T>> {

	public final PlayerCapabilityHolder<T> holder;

	public PlayerCapabilityNetworkHandler(PlayerCapabilityHolder<T> holder) {
		this.holder = holder;
	}

	public void toClient(ServerPlayer e) {
		L2Core.PACKET_HANDLER.toClientPlayer(PlayerCapToClient.of(e, PlayerCapToClient.Action.CLIENT, holder, holder.get(e)), e);
	}

	public void toTracking(ServerPlayer e) {
		L2Core.PACKET_HANDLER.toTrackingOnly(PlayerCapToClient.of(e, PlayerCapToClient.Action.TRACK, holder, holder.get(e)), e);
	}

	public void startTracking(ServerPlayer tracker, ServerPlayer target) {
		L2Core.PACKET_HANDLER.toClientPlayer(PlayerCapToClient.of(target, PlayerCapToClient.Action.TRACK, holder, holder.get(target)), tracker);
	}

}

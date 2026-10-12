package dev.xkmc.l2core.capability.player;

import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;

import java.util.UUID;
import java.util.function.Predicate;

public class ClientSyncHandler {

	public static <T extends PlayerCapabilityTemplate<T>> void parse(
			RegistryAccess access, byte[] tag, PlayerCapabilityHolder<T> holder, Predicate<SerialField> pred, UUID playerId) {
		var level = Minecraft.getInstance().level;
		if (level == null) return;
		var player = level.getPlayerByUUID(playerId);
		if (player == null) return;
		PacketCodec.fromBytes(access, tag, holder.cls(), player.getData(holder.type()), pred);
	}

}

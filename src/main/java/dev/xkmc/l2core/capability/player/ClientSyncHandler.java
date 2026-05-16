package dev.xkmc.l2core.capability.player;

import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;

import java.util.function.Predicate;

public class ClientSyncHandler {

	public static <T extends PlayerCapabilityTemplate<T>> void parse(
			RegistryAccess access, byte[] tag, PlayerCapabilityHolder<T> holder, Predicate<SerialField> pred) {
		var player = Minecraft.getInstance().player;
		if (player == null) return;
		PacketCodec.fromBytes(access, tag, holder.cls(), player.getData(holder.type()), pred);
	}

}

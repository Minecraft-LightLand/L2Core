package dev.xkmc.l2core.capability.player;

import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.core.RegistryAccess;

import java.util.function.Predicate;

public class ClientSyncHandler {

	public static <T extends PlayerCapabilityTemplate<T>> void parse(
			RegistryAccess access, byte[] tag, PlayerCapabilityHolder<T> holder, Predicate<SerialField> pred) {
		PacketCodec.fromBytes(access, tag, holder.cls(), Proxy.getClientPlayer().getData(holder.type()), pred);
	}

}

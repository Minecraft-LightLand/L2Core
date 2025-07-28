package dev.xkmc.l2core.capability.conditionals;

import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.world.entity.player.Player;

public class ClientDataHandler {

	public static <T extends ConditionalToken> void handle(TokenKey<T> key, byte[] data) {
		Player player = Proxy.getClientPlayer();
		if (player == null) return;
		var buf = PacketCodec.decode(player.registryAccess(), data);
		T token = Wrappers.cast(PacketCodec.from(buf, ConditionalToken.class, null));
		ConditionalToken old = L2LibReg.CONDITIONAL.type().getOrCreate(player).data.put(key, token);
		if (token instanceof NetworkSensitiveToken<?> t) {
			t.onSync(Wrappers.cast(old), player);
		}
	}

}

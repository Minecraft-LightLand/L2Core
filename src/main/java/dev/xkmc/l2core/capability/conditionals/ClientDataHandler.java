package dev.xkmc.l2core.capability.conditionals;

import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.world.entity.player.Player;

public class ClientDataHandler {

	public static <T extends ConditionalToken> void handle(TokenKey<T> key, T token) {
		Player player = Proxy.getClientPlayer();
		if (player == null) return;
		ConditionalToken old = L2LibReg.CONDITIONAL.type().getOrCreate(player).data.put(key, token);
		if (token instanceof NetworkSensitiveToken<?> t) {
			t.onSync(Wrappers.cast(old), player);
		}
	}

}

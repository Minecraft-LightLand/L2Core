package dev.xkmc.l2core.capability.conditionals;

import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

public class ClientDataHandler {

	public static <T extends ConditionalToken> void handle(TokenKey<T> key, int id, byte[] data) {
		var level = Minecraft.getInstance().level;
		if (level == null) return;
		var e = level.getEntity(id);
		if (!(e instanceof LivingEntity le)) return;
		var cap = ConditionalData.of(le);
		var buf = PacketCodec.decode(e.registryAccess(), data);
		T token = Wrappers.cast(PacketCodec.from(buf, ConditionalToken.class, null));
		ConditionalToken old = cap.put(key, token);
		if (token instanceof NetworkSensitiveToken<?> t) {
			t.onSync(Wrappers.cast(old), le);
		}
	}

}

package dev.xkmc.l2core.capability.conditionals;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public record TokenToClient(Identifier id, byte[] data)
		implements SerialPacketBase<TokenToClient> {

	public static <T extends ConditionalToken> TokenToClient of(RegistryAccess access, TokenKey<T> key, T token) {
		var data = PacketCodec.toBytes(access, token, ConditionalToken.class, e -> true);
		return new TokenToClient(key.asLocation(), data);
	}

	@Override
	public void handle(@Nullable Player player) {
		ClientDataHandler.handle(TokenKey.of(id), data);
	}

}

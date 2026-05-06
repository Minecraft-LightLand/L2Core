package dev.xkmc.l2core.capability.conditionals;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public record TokenToClient(Identifier id, ConditionalToken token)
		implements SerialPacketBase<TokenToClient> {

	public static <T extends ConditionalToken> TokenToClient of(TokenKey<T> key, T token) {
		return new TokenToClient(key.asLocation(), token);
	}

	@Override
	public void handle(@Nullable Player player) {
		ClientDataHandler.handle(TokenKey.of(id), token);
	}

}

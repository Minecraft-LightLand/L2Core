package dev.xkmc.l2core.capability.player;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityTemplate;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.world.entity.player.Player;

@SerialClass
public class PlayerCapabilityTemplate<T extends PlayerCapabilityTemplate<T>> extends GeneralCapabilityTemplate<Player, T> {

	public void init(Player player) {
	}

	public void onClone(Player player, boolean isWasDeath) {

	}

}

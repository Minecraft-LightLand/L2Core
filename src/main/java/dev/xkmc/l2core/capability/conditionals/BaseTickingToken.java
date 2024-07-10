package dev.xkmc.l2core.capability.conditionals;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.world.entity.player.Player;

@SerialClass
public class BaseTickingToken extends ConditionalToken {

	@SerialField
	public int tick = 0;

	public BaseTickingToken() {

	}

	public BaseTickingToken(int time) {
		this.tick = time;
	}

	@Override
	public boolean tick(Player player) {
		if (tick > 0) {
			tick--;
			onTick(player);
			return false;
		} else {
			onRemove(player);
			return true;
		}
	}

	public void onTick(Player player) {

	}

	public void onRemove(Player player) {

	}

}

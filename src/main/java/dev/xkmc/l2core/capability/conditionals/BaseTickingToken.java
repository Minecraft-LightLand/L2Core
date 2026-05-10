package dev.xkmc.l2core.capability.conditionals;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.world.entity.LivingEntity;

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
	public boolean tick(LivingEntity entity) {
		if (tick > 0) {
			tick--;
			onTick(entity);
			return false;
		} else {
			onRemove(entity);
			return true;
		}
	}

	public void onTick(LivingEntity entity) {

	}

	public void onRemove(LivingEntity entity) {

	}

}

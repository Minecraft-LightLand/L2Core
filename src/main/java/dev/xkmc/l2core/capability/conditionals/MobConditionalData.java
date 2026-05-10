package dev.xkmc.l2core.capability.conditionals;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityTemplate;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.world.entity.LivingEntity;

import java.util.LinkedHashMap;

@SerialClass
public class MobConditionalData extends GeneralCapabilityTemplate<LivingEntity, MobConditionalData> implements ConditionalData {

	@SerialField
	protected LinkedHashMap<TokenKey<?>, ConditionalToken> data = new LinkedHashMap<>();

	public LinkedHashMap<TokenKey<?>, ConditionalToken> data() {
		return data;
	}

	@Override
	public void tick(LivingEntity entity) {
		tickLogic(entity);
	}

}

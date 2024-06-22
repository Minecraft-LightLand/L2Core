package dev.xkmc.l2core.base.effects;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityTemplate;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

@SerialClass
public class ClientEffectCap extends GeneralCapabilityTemplate<LivingEntity, ClientEffectCap> {

	public final Map<Holder<MobEffect>, Integer> map = new TreeMap<>(Comparator.comparing(Holder::getRegisteredName));

}

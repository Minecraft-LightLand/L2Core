package dev.xkmc.l2core.base.effects;

import dev.xkmc.l2core.base.effects.api.ForceEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.function.Predicate;

public class EffectUtil {

	/**
	 * force add effect, make hard not override
	 * for icon use only, such as Arcane Mark on Wither and Ender Dragon
	 */
	private static void forceAddEffect(LivingEntity e, MobEffectInstance ins, @Nullable Entity source) {
		MobEffectInstance old = e.activeEffects.get(ins.getEffect());
		var event = new ForceAddEffectEvent(e, ins);
		NeoForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			return;
		}
		NeoForge.EVENT_BUS.post(new MobEffectEvent.Added(e, old, ins, source));
		if (old == null) {
			e.activeEffects.put(ins.getEffect(), ins);
			e.onEffectAdded(ins, source);
			ins.onEffectAdded(e);
		} else if (old.update(ins)) {
			e.onEffectUpdated(old, true, source);
		}
		ins.onEffectStarted(e);
	}

	public static void addEffect(LivingEntity entity, MobEffectInstance ins, @Nullable Entity source) {
		ins = new MobEffectInstance(ins.getEffect(), ins.getDuration(), ins.getAmplifier(),
				ins.isAmbient(), ins.isVisible(), ins.showIcon());
		if (ins.getEffect() instanceof ForceEffect)
			forceAddEffect(entity, ins, source);
		else if (ins.getEffect().value().isInstantenous())
			ins.getEffect().value().applyInstantenousEffect(null, null, entity, ins.getAmplifier(), 1);
		else entity.addEffect(ins, source);
	}

	public static void refreshEffect(LivingEntity entity, MobEffectInstance ins, Entity source) {
		if (ins.duration < 40) ins.duration = 40;
		MobEffectInstance cur = entity.getEffect(ins.getEffect());
		if (cur == null ||
				cur.getAmplifier() < ins.getAmplifier() ||
				cur.getAmplifier() == ins.getAmplifier() &&
						cur.getDuration() < ins.getDuration() / 2
		) addEffect(entity, ins, source);
	}

	public static void removeEffect(LivingEntity entity, Predicate<MobEffectInstance> pred) {
		Iterator<MobEffectInstance> itr = entity.activeEffects.values().iterator();
		while (itr.hasNext()) {
			MobEffectInstance effect = itr.next();
			if (pred.test(effect) && EventHooks.onEffectRemoved(entity, effect, null)) {
				entity.onEffectRemoved(effect);
				itr.remove();
				entity.effectsDirty = true;
			}
		}
	}

}

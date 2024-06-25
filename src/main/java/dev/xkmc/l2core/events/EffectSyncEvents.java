package dev.xkmc.l2core.events;

import dev.xkmc.l2core.base.effects.EffectToClient;
import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2core.init.L2TagGen;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = L2Core.MODID, bus = EventBusSubscriber.Bus.GAME)
public class EffectSyncEvents {

	private static boolean isTracked(Holder<MobEffect> eff) {
		return eff.is(L2TagGen.TRACKED_EFFECTS);
	}

	@SubscribeEvent
	public static void onPotionAddedEvent(MobEffectEvent.Added event) {
		var ins = event.getEffectInstance();
		if (ins == null) return;
		if (isTracked(ins.getEffect())) {
			onEffectAppear(event.getEffectInstance().getEffect(), event.getEntity(), event.getEffectInstance().getAmplifier());
		}
	}

	@SubscribeEvent
	public static void onPotionRemoveEvent(MobEffectEvent.Remove event) {
		if (event.getEffectInstance() != null && isTracked(event.getEffectInstance().getEffect())) {
			onEffectDisappear(event.getEffectInstance().getEffect(), event.getEntity());
		}
	}

	@SubscribeEvent
	public static void onPotionExpiryEvent(MobEffectEvent.Expired event) {
		if (event.getEffectInstance() != null && isTracked(event.getEffectInstance().getEffect())) {
			onEffectDisappear(event.getEffectInstance().getEffect(), event.getEntity());
		}
	}

	@SubscribeEvent
	public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
		if (!(event.getTarget() instanceof LivingEntity le)) return;
		for (var eff : le.getActiveEffectsMap().keySet()) {
			if (isTracked(eff)) {
				onEffectAppear(eff, le, le.getActiveEffectsMap().get(eff).getAmplifier());
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerStopTracking(PlayerEvent.StopTracking event) {
		if (!(event.getTarget() instanceof LivingEntity le)) return;
		for (var eff : le.getActiveEffectsMap().keySet()) {
			if (isTracked(eff)) {
				onEffectDisappear(eff, le);
			}
		}
	}

	@SubscribeEvent
	public static void onServerPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		ServerPlayer e = (ServerPlayer) event.getEntity();
		if (e == null) return;
		for (var eff : e.getActiveEffectsMap().keySet()) {
			if (isTracked(eff)) {
				onEffectAppear(eff, e, e.getActiveEffectsMap().get(eff).getAmplifier());
			}
		}
	}

	@SubscribeEvent
	public static void onServerPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
		ServerPlayer e = (ServerPlayer) event.getEntity();
		if (e == null) return;
		for (var eff : e.getActiveEffectsMap().keySet()) {
			if (isTracked(eff)) {
				onEffectDisappear(eff, e);
			}
		}
	}

	private static void onEffectAppear(Holder<MobEffect> eff, LivingEntity e, int lv) {
		if (e.level().isClientSide()) return;
		L2Core.PACKET_HANDLER.toTrackingPlayers(new EffectToClient(e.getId(), eff, true, lv), e);
	}

	private static void onEffectDisappear(Holder<MobEffect> eff, LivingEntity e) {
		if (e.level().isClientSide()) return;
		L2Core.PACKET_HANDLER.toTrackingPlayers(new EffectToClient(e.getId(), eff, false, 0), e);
	}

}

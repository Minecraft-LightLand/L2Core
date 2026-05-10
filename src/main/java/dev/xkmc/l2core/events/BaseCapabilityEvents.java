package dev.xkmc.l2core.events;

import dev.xkmc.l2core.capability.conditionals.ConditionalData;
import dev.xkmc.l2core.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2core.init.L2LibReg;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = L2Core.MODID)
public class BaseCapabilityEvents {

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		if (event.getEntity().isAlive())
			for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
				holder.getOrCreate(event.getEntity()).tick(event.getEntity());
			}
	}

	@SubscribeEvent
	public static void onEntityTick(EntityTickEvent.Post event) {
		if (!(event.getEntity() instanceof LivingEntity le)) return;
		if (le instanceof Player) return;
		if (!le.isAlive()) return;
		var cap = L2LibReg.MOB_CONDITIONAL.type().getExisting(le);
		if (cap.isEmpty()) return;
		cap.get().tick(le);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onPlayerClone(PlayerEvent.Clone event) {
		for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
			ServerPlayer e = (ServerPlayer) event.getEntity();
			holder.getOrCreate(e).onClone(e, event.isWasDeath());
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onPlayerJoinLevel(EntityJoinLevelEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer e)) return;
		for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
			holder.network.toClient(e);
			holder.network.toTracking(e);
		}
	}

	@SubscribeEvent
	public static void onServerPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		ServerPlayer e = (ServerPlayer) event.getEntity();
		if (e == null) return;
		for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
			holder.getOrCreate(e).init(e);
			holder.network.toClient(e);
			holder.network.toTracking(e);
		}
	}

	@SubscribeEvent
	public static void onStartTracking(PlayerEvent.StartTracking event) {
		for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
			if (!(event.getTarget() instanceof ServerPlayer e)) continue;
			holder.network.startTracking((ServerPlayer) event.getEntity(), e);
		}
		var obs = event.getEntity();
		var e = event.getTarget();
		if (e instanceof LivingEntity le && obs instanceof ServerPlayer sp) {
			var cap = ConditionalData.ofNullable(le);
			if (cap.isEmpty()) return;
			cap.get().onStartTracking(le, sp);
		}
	}

}

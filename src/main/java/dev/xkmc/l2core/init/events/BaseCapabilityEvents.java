package dev.xkmc.l2core.init.events;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2core.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = L2Core.MODID, bus = EventBusSubscriber.Bus.GAME)
public class BaseCapabilityEvents {

	@SubscribeEvent
	public static void onLivingTick(EntityTickEvent.Post event) {
		if (event.getEntity() instanceof LivingEntity le && le.isAlive()) {
			for (GeneralCapabilityHolder<?, ?> holder : GeneralCapabilityHolder.INTERNAL_MAP.values()) {
				if (holder.isFor(event.getEntity()))
					holder.get(Wrappers.cast(event.getEntity())).tick(Wrappers.cast(event.getEntity()));
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onPlayerClone(PlayerEvent.Clone event) {
		for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
			ServerPlayer e = (ServerPlayer) event.getEntity();
			holder.get(e).onClone(e, event.isWasDeath());
			holder.network.toClient(e);
			holder.network.toTracking(e);
		}
	}

	@SubscribeEvent
	public static void onServerPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		ServerPlayer e = (ServerPlayer) event.getEntity();
		if (e == null) return;
		for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
			holder.get(e).init(e);
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
	}

}

package dev.xkmc.l2core.events;

import dev.xkmc.l2core.content.explosion.BaseExplosion;
import dev.xkmc.l2core.content.raytrace.RayTraceUtil;
import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2core.util.FlagMarker;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = L2Core.MODID)
public class MiscServerEventHandler {

	@SubscribeEvent
	public static void serverTick(ServerTickEvent.Post event) {
		RayTraceUtil.serverTick(event.getServer());
	}

	@SubscribeEvent
	public static void onDetonate(ExplosionEvent.Detonate event) {
		if (event.getExplosion() instanceof BaseExplosion exp) {
			event.getAffectedEntities().removeIf(e -> !exp.hurtEntity(e));
		}
	}

	@SubscribeEvent
	public static void onEntityStruck(EntityStruckByLightningEvent event) {
		if (event.getLightning().entityTags().contains(FlagMarker.LIGHTNING)) {
			if (!(event.getEntity() instanceof LivingEntity) || event.getEntity() == event.getLightning().getCause()) {
				event.setCanceled(true);
			}
		}
	}

}

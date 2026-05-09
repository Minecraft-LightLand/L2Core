package dev.xkmc.l2core.events;

import dev.xkmc.l2core.content.raytrace.EntityTarget;
import dev.xkmc.l2core.init.L2Core;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = L2Core.MODID)
public class MiscClientEventHandler {

	@SubscribeEvent
	public static void clientTick(ClientTickEvent.Post event) {
		for (EntityTarget target : EntityTarget.LIST) {
			target.tickRender();
		}
	}

}

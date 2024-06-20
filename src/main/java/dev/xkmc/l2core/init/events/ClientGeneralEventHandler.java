package dev.xkmc.l2core.init.events;

import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2core.util.raytrace.EntityTarget;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = L2Core.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientGeneralEventHandler {

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		for (EntityTarget target : EntityTarget.LIST) {
			target.tickRender();
		}
	}

}

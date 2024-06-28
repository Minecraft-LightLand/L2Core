package dev.xkmc.l2core.events;

import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2core.serial.config.PacketHandlerWithConfig;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

@EventBusSubscriber(modid = L2Core.MODID, bus = EventBusSubscriber.Bus.GAME)
public class SchedulerHandler {

	@SubscribeEvent
	public static void addReloadListeners(AddReloadListenerEvent event) {
		PacketHandlerWithConfig.addReloadListeners(event);
	}

	@SubscribeEvent
	public static void onDatapackSync(OnDatapackSyncEvent event) {
		PacketHandlerWithConfig.onDatapackSync(event);
	}

	@SubscribeEvent
	public static void serverTick(ServerTickEvent.Post event) {
		execute();
	}

	private static List<BooleanSupplier> TASKS = new ArrayList<>();

	public static synchronized void schedule(Runnable runnable) {
		TASKS.add(() -> {
			runnable.run();
			return true;
		});
	}

	public static synchronized void schedulePersistent(BooleanSupplier runnable) {
		TASKS.add(runnable);
	}

	private static synchronized void execute() {
		if (TASKS.isEmpty()) return;
		var temp = TASKS;
		TASKS = new ArrayList<>();
		temp.removeIf(BooleanSupplier::getAsBoolean);
		temp.addAll(TASKS);
		TASKS = temp;
	}

}

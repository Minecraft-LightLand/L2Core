package dev.xkmc.l2core.init;

import dev.xkmc.l2core.base.effects.EffectToClient;
import dev.xkmc.l2core.capability.conditionals.TokenToClient;
import dev.xkmc.l2core.capability.player.PlayerCapToClient;
import dev.xkmc.l2core.serial.config.SyncPacket;
import dev.xkmc.l2serial.network.PacketHandler;
import dev.xkmc.l2serial.serialization.custom_handler.Handlers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static dev.xkmc.l2serial.network.PacketHandler.NetDir.PLAY_TO_CLIENT;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(L2Core.MODID)
@EventBusSubscriber(modid = L2Core.MODID, bus = EventBusSubscriber.Bus.MOD)
public class L2Core {

	public static final String MODID = "l2core";
	public static final Logger LOGGER = LogManager.getLogger();

	// TODO public static final L2Registrate REGISTRATE = new L2Registrate(MODID);

	public static final PacketHandler PACKET_HANDLER = new PacketHandler(MODID, 1,
			e -> e.create(EffectToClient.class, PLAY_TO_CLIENT),
			e -> e.create(PlayerCapToClient.class, PLAY_TO_CLIENT),
			e -> e.create(TokenToClient.class, PLAY_TO_CLIENT)
	);

	public L2Core(IEventBus bus) {
		Handlers.register();
		L2LibReg.register(bus);
	}

	@SubscribeEvent
	public static void onPacketReg(RegisterPayloadHandlersEvent event) {
		PACKET_HANDLER.register(event);
	}

}

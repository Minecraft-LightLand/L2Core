package dev.xkmc.l2core.init.reg.syncreg;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public record SyncRegistryConfigTask(ServerConfigurationPacketListener listener) implements ConfigurationTask {

	public static final Type TYPE = new ConfigurationTask.Type("synchronize_registry_config");
	public static final Logger LOGGER = LogUtils.getLogger();

	public static synchronized void handleData(RegistryConfigDataPayload payload, IPayloadContext ctx) {
		SyncRegistryConfigClientHandler.handleData(payload, ctx);
	}

	public static void addTask(ServerConfigurationPacketListener listener, Consumer<ConfigurationTask> tasks) {
		if (listener.hasChannel(RegistryConfigDataPayload.TYPE) &&
				listener.hasChannel(RegistryConfigAckPayload.TYPE)) {
			tasks.accept(new SyncRegistryConfigTask(listener));
		}
	}

	public static void handleAck(RegistryConfigAckPayload payload, IPayloadContext ctx) {
		ctx.finishCurrentTask(TYPE);
	}

	@Override
	public synchronized void start(Consumer<Packet<?>> packetSender) {
		if (listener.getConnection().isMemoryConnection()) {
			listener.finishCurrentTask(TYPE);
			return;
		}
		Map<ResourceLocation, JsonElement> configs = new TreeMap<>();
		for (var ent : RegistryConfigHandlers.MAP.entrySet()) {
			try {
				JsonElement data = ent.getValue().serializeConfig();
				configs.put(ent.getKey(), data);
			} catch (Exception e) {
				LOGGER.error("Failed to serialize " + ent.getKey(), e);
				listener.disconnect(Component.literal("Failed to serialize " + ent.getKey()));
				return;
			}
		}
		packetSender.accept(new RegistryConfigDataPayload(configs).toVanillaClientbound());
	}

	@Override
	public Type type() {
		return TYPE;
	}

}

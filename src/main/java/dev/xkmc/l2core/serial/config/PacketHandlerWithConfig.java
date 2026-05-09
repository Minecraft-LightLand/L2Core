package dev.xkmc.l2core.serial.config;

import com.google.gson.JsonElement;
import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2serial.network.PacketHandler;
import dev.xkmc.l2serial.serialization.codec.JsonCodec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@SuppressWarnings("unused")
public class PacketHandlerWithConfig extends PacketHandler {

	static final Map<String, PacketHandlerWithConfig> INTERNAL = new ConcurrentHashMap<>();

	public static void onDatapackSync(OnDatapackSyncEvent event) {
		for (PacketHandlerWithConfig handler : INTERNAL.values()) {
			SyncPacket packet = new SyncPacket(handler.modid, handler.configs);
			if (event.getPlayer() == null) L2Core.PACKET_HANDLER.toAllClient(packet);
			else L2Core.PACKET_HANDLER.toClientPlayer(packet, event.getPlayer());
		}
	}

	public static void addReloadListeners(AddServerReloadListenersEvent event) {
		for (PacketHandlerWithConfig handler : INTERNAL.values()) {
			if (handler.listener != null)
				event.addListener(L2Core.loc(handler.modid), handler.listener);
		}
	}

	public ArrayList<ConfigInstance> configs = new ArrayList<>();

	public final String config_path;

	final ConfigReloadListener listener;
	final List<Runnable> listener_before = new ArrayList<>();
	final List<Runnable> listener_after = new ArrayList<>();
	final Map<String, BaseConfigType<?>> types = new HashMap<>();

	@SafeVarargs
	public PacketHandlerWithConfig(String id, int version, Function<PacketHandler, PacketConfiguration<?>>... values) {
		super(id, version, values);
		INTERNAL.put(id, this);
		config_path = id + "_config";
		listener = new ConfigReloadListener(config_path);
		listener_before.add(configs::clear);
	}

	public void addBeforeReloadListener(Runnable runnable) {
		listener_before.add(runnable);
	}

	public void addAfterReloadListener(Runnable runnable) {
		listener_after.add(runnable);
	}

	public <T extends BaseConfig> void addConfig(String id, Class<T> loader) {
		BaseConfigType<T> c = new BaseConfigType<>(this, id, loader);
		types.put(id, c);
		addBeforeReloadListener(c::beforeReload);
		addAfterReloadListener(c::afterReload);
	}

	public <T extends BaseConfig> void addCachedConfig(String id, Class<T> loader) {
		MergedConfigType<T> c = new MergedConfigType<>(this, id, loader);
		types.put(id, c);
		addBeforeReloadListener(c::beforeReload);
		addAfterReloadListener(c::afterReload);
	}

	<T extends BaseConfig> T getCachedConfig(String id) {
		MergedConfigType<T> type = Wrappers.cast(types.get(id));
		return type.load();
	}

	class ConfigReloadListener extends JsonResourceReloadListener {

		protected ConfigReloadListener(String path) {
			super(FileToIdConverter.json(path));
		}

		@Override
		protected void apply(Map<Identifier, JsonElement> map, ResourceManager manager, ProfilerFiller filler) {
			listener_before.forEach(Runnable::run);
			map.forEach((k, v) -> {
				String id = k.getPath().split("/")[0];
				if (types.containsKey(id)) {
					String name = k.getPath().substring(id.length() + 1);
					Identifier nk = k.withPath(name);
					var type = types.get(id);
					var ans = new JsonCodec(getRegistryLookup()).from(v, type.cls, null);
					if (ans != null) addServerConfig(type, nk, Wrappers.cast(ans));
				}
			});
			listener_after.forEach(Runnable::run);
		}

		private <T extends BaseConfig> void addServerConfig(BaseConfigType<T> type, Identifier k, T config) {
			config.id = k;
			type.configs.put(k, config);
			configs.add(new ConfigInstance(type.id, k, config));
		}

		private <T extends BaseConfig> void addClientConfig(BaseConfigType<T> type, Identifier k, T config) {
			config.id = k;
			type.clientConfigs.put(k, config);
		}

		/**
		 * Called on client side only
		 */
		public void apply(ArrayList<ConfigInstance> list) {
			types.values().forEach(BaseConfigType::clientBeforeReload);
			for (var e : list) {
				addClientConfig(types.get(e.name), e.id(), Wrappers.cast(e.config));
			}
			types.values().forEach(BaseConfigType::clientAfterReload);
		}
	}

	public record ConfigInstance(String name, Identifier id, BaseConfig config) {

	}

}

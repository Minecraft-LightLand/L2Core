package dev.xkmc.l2core.serial.config;

import dev.xkmc.l2core.util.ServerProxy;
import net.minecraft.resources.Identifier;

public class MergedConfigType<T extends BaseConfig> extends BaseConfigType<T> {

	private T result, clientResult;

	MergedConfigType(PacketHandlerWithConfig parent, String id, Class<T> cls) {
		super(parent, id, cls);
	}

	T load() {
		if (ServerProxy.isOnClient())
			return loadClient();
		return loadServer();
	}

	T loadServer() {
		if (result != null) {
			return result;
		}
		result = new ConfigMerger<>(cls).apply(configs.values());
		result.id = Identifier.fromNamespaceAndPath(parent.modid, id);
		return result;
	}

	T loadClient() {
		if (clientResult != null) {
			return clientResult;
		}
		clientResult = new ConfigMerger<>(cls).apply(clientConfigs.values());
		clientResult.id = ResourceLocation.fromNamespaceAndPath(parent.modid, id);
		return clientResult;
	}

	@Override
	public void afterReload() {
		result = null;
		if (cls.isAnnotationPresent(ConfigLoadOnStart.class)) {
			loadServer();
		}
	}

	@Override
	public void clientAfterReload() {
		clientResult = null;
		if (cls.isAnnotationPresent(ConfigLoadOnStart.class)) {
			loadClient();
		}
	}

}

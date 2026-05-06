package dev.xkmc.l2core.serial.config;

import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class BaseConfigType<T extends BaseConfig> {

	public final Class<T> cls;
	public final String id;
	public final PacketHandlerWithConfig parent;

	final Map<Identifier, T> configs = new HashMap<>();
	final Map<Identifier, T> clientConfigs = new HashMap<>();

	protected BaseConfigType(PacketHandlerWithConfig parent, String id, Class<T> cls) {
		this.parent = parent;
		this.id = id;
		this.cls = cls;
	}

	public void beforeReload() {
		configs.clear();
	}

	public void afterReload() {
	}

	public void clientBeforeReload() {
		clientConfigs.clear();
	}

	public void clientAfterReload() {
	}

}

package dev.xkmc.l2core.serial.configval;

import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2serial.util.Wrappers;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AbstractConfigParser {

	private static Map<String, ModConfig> getMap() {
		try {
			return Wrappers.cast(ConfigTracker.class.getDeclaredField("fileMap").get(ConfigTracker.INSTANCE));
		} catch (Exception e) {
			L2Core.LOGGER.throwing(e);
			return Map.of();
		}
	}

	public static Optional<Object> parse(String path, List<String> line) {
		var map = getMap();
		var file = map.get(path);
		if (file == null) {
			L2Core.LOGGER.warn("File {} is not a config file", path);
			return Optional.empty();
		}
		var spec = file.getSpec();
		if (!(spec instanceof ModConfigSpec modSpec) || !modSpec.isLoaded()){
			L2Core.LOGGER.warn("File {} is not a loaded config file", path);
			return Optional.empty();
		}
		return Optional.ofNullable(modSpec.getValues().get(line));
	}

}

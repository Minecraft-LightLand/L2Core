package dev.xkmc.l2core.serial.configval;

import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2core.util.ConfigInit;
import dev.xkmc.l2serial.util.Wrappers;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AbstractConfigParser {

	private static Map<String, ModConfig> getMap() {
		try {
			var field = ConfigTracker.class.getDeclaredField("fileMap");
			field.setAccessible(true);
			return Wrappers.cast(field.get(ConfigTracker.INSTANCE));
		} catch (Exception e) {
			L2Core.LOGGER.throwing(e);
			return Map.of();
		}
	}

	@Nullable
	private static IConfigSpec getSpec(String path) {
		var init = ConfigInit.get(path);
		if (init != null) return init.getSpec();
		var map = getMap();
		var file = map.get(path);
		if (file == null) {
			L2Core.LOGGER.warn("File {} is not a config file", path);
			return null;
		}
		return file.getSpec();
	}

	public static Optional<Object> parse(String path, List<String> line) {
		var spec = getSpec(path);
		if (!(spec instanceof ModConfigSpec modSpec) || !modSpec.isLoaded()) {
			L2Core.LOGGER.warn("File {} is not a loaded config file", path);
			return Optional.empty();
		}
		return Optional.<ModConfigSpec.ConfigValue<?>>ofNullable(modSpec.getValues().get(line))
				.map(ModConfigSpec.ConfigValue::get);
	}

}

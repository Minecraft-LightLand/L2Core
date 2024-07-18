package dev.xkmc.l2core.serial.configval;

import dev.xkmc.l2core.mixin.ConfigTrackerAccessor;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Optional;

public class AbstractConfigParser {

	public static Optional<Object> parse(String path, List<String> line) {
		var file = ((ConfigTrackerAccessor) ConfigTracker.INSTANCE).getFileMap().get(path);
		if (file == null) return Optional.empty();
		var spec = file.getSpec();
		if (!(spec instanceof ModConfigSpec modSpec) || !modSpec.isLoaded()) return Optional.empty();
		return Optional.ofNullable(modSpec.getValues().get(line));
	}

}

package dev.xkmc.l2core.serial.configval;

import net.neoforged.fml.config.ConfigTracker;

import java.util.List;
import java.util.Optional;

public class AbstractConfigParser {

    public static Optional<Object> parse(String path, List<String> line) {
        return Optional.ofNullable(ConfigTracker.INSTANCE.fileMap().get(path))
            .map(file -> file.getConfigData().get(line));
    }

}

package dev.xkmc.l2core.serial.conditions;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2core.init.L2LibReg;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;

public record StringValueCondition(String path, ArrayList<String> line, String key) implements ICondition {

	public static StringValueCondition of(String file, ModConfigSpec.ConfigValue<String> config, String key) {
		return new StringValueCondition(file, new ArrayList<>(config.getPath()), key);
	}

	@Override
	public boolean test(IContext context) {
		var file = ConfigTracker.INSTANCE.fileMap().get(path);
		if (file == null) return false;
		var line = file.getConfigData().get(line());
		if (line == null) return false;
		return line instanceof String val && val.equals(key);
	}

	@Override
	public MapCodec<StringValueCondition> codec() {
		return L2LibReg.CONDITION_STR.get();
	}

}

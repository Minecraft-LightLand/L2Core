package dev.xkmc.l2core.serial.configval;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2core.init.L2LibReg;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;

public record StringValueCondition(String path, ArrayList<String> line, String key) implements ICondition {

	public static StringValueCondition of(String file, ModConfigSpec.ConfigValue<String> config, String key) {
		return new StringValueCondition(file, new ArrayList<>(config.getPath()), key);
	}

	@Override
	public boolean test(IContext context) {
		return AbstractConfigParser.parse(path, line)
				.map(e -> e instanceof String val && val.equals(key)).orElse(false);
	}

	@Override
	public MapCodec<StringValueCondition> codec() {
		return L2LibReg.CONDITION_STR.get();
	}

}

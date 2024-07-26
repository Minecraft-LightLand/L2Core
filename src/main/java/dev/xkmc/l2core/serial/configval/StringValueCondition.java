package dev.xkmc.l2core.serial.configval;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.util.ConfigInit;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.function.Function;

public record StringValueCondition(String path, ArrayList<String> line, String key) implements ICondition {

	public static <T extends ConfigInit> StringValueCondition of(T file, Function<T, ModConfigSpec.ConfigValue<Double>> config, String key) {
		return new StringValueCondition(file.getPath(), new ArrayList<>(config.apply(file).getPath()), key);
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

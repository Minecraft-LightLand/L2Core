package dev.xkmc.l2core.serial.configval;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.util.ConfigInit;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public record ListStringValueCondition(String path, ArrayList<String> line, String key) implements ICondition {

	public static <T extends ConfigInit> ListStringValueCondition of(T file, Function<T, ModConfigSpec.ConfigValue<Double>> config, String key) {
		return new ListStringValueCondition(file.getPath(), new ArrayList<>(config.apply(file).getPath()), key);
	}

	@Override
	public boolean test(IContext context) {
		return AbstractConfigParser.parse(path, line)
				.map(e -> e instanceof List<?> val && val.contains(key)).orElse(false);
	}

	@Override
	public MapCodec<ListStringValueCondition> codec() {
		return L2LibReg.CONDITION_LIST_STR.get();
	}

}

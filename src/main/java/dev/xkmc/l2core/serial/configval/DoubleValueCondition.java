package dev.xkmc.l2core.serial.configval;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.util.ConfigInit;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.function.Function;

public record DoubleValueCondition(String path, ArrayList<String> line, double low, double high) implements ICondition {

	public static <T extends ConfigInit> DoubleValueCondition of(T file, Function<T, ModConfigSpec.ConfigValue<Double>> config, double low, double high) {
		return new DoubleValueCondition(file.getPath(), new ArrayList<>(config.apply(file).getPath()), low, high);
	}

	@Override
	public boolean test(IContext context) {
		return AbstractConfigParser.parse(path, line)
				.map(e -> e instanceof Double val && low <= val && val <= high).orElse(false);
	}

	@Override
	public MapCodec<DoubleValueCondition> codec() {
		return L2LibReg.CONDITION_DOUBLE.get();
	}

}

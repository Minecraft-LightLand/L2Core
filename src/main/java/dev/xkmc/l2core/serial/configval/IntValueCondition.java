package dev.xkmc.l2core.serial.configval;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.util.ConfigInit;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.function.Function;

public record IntValueCondition(String path, ArrayList<String> line, int low, int high) implements ICondition {

	public static <T extends ConfigInit> IntValueCondition of(T file, Function<T, ModConfigSpec.ConfigValue<Double>> config, int low, int high) {
		return new IntValueCondition(file.getPath(), new ArrayList<>(config.apply(file).getPath()), low, high);
	}

	@Override
	public boolean test(IContext context) {
		return AbstractConfigParser.parse(path, line)
				.map(e -> e instanceof Integer val && low <= val && val <= high).orElse(false);
	}


	@Override
	public MapCodec<IntValueCondition> codec() {
		return L2LibReg.CONDITION_INT.get();
	}


}

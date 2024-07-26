package dev.xkmc.l2core.serial.configval;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.util.ConfigInit;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.function.Function;

public record BooleanValueCondition(String path, ArrayList<String> line, boolean expected) implements ICondition {

	public static <T extends ConfigInit> BooleanValueCondition of(T file, Function<T, ModConfigSpec.ConfigValue<Boolean>> config, boolean value) {
		return new BooleanValueCondition(file.getPath(), new ArrayList<>(config.apply(file).getPath()), value);
	}

	@Override
	public boolean test(IContext context) {
		return AbstractConfigParser.parse(path, line)
				.map(e -> e instanceof Boolean bool && bool == expected).orElse(false);
	}

	@Override
	public MapCodec<BooleanValueCondition> codec() {
		return L2LibReg.CONDITION_BOOL.get();
	}

}

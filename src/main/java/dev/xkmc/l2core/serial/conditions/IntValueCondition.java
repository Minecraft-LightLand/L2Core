package dev.xkmc.l2core.serial.conditions;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2core.init.L2LibReg;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;

public record IntValueCondition(String path, ArrayList<String> line, int low, int high) implements ICondition {

	public static IntValueCondition of(String file, ModConfigSpec.ConfigValue<Integer> config, int low, int high) {
		return new IntValueCondition(file, new ArrayList<>(config.getPath()), low, high);
	}

	@Override
	public boolean test(IContext context) {
		var file = ConfigTracker.INSTANCE.fileMap().get(path);
		if (file == null) return false;
		var line = file.getConfigData().get(line());
		if (line == null) return false;
		return line instanceof Integer val && low <= val && val <= high;
	}


	@Override
	public MapCodec<IntValueCondition> codec() {
		return L2LibReg.CONDITION_INT.get();
	}


}

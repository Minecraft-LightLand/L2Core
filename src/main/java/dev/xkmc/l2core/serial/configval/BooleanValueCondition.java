package dev.xkmc.l2core.serial.configval;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2core.init.L2LibReg;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;

public record BooleanValueCondition(String path, ArrayList<String> line, boolean expected) implements ICondition {

    public static BooleanValueCondition of(String file, ModConfigSpec.ConfigValue<Boolean> config, boolean value) {
        return new BooleanValueCondition(file, new ArrayList<>(config.getPath()), value);
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

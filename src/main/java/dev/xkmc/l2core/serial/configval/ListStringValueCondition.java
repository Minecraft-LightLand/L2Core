package dev.xkmc.l2core.serial.configval;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2core.init.L2LibReg;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.List;

public record ListStringValueCondition(String path, ArrayList<String> line, String key) implements ICondition {

    public static ListStringValueCondition of(String file, ModConfigSpec.ConfigValue<List<String>> config, String key) {
        return new ListStringValueCondition(file, new ArrayList<>(config.getPath()), key);
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

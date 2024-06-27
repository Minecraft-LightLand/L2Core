package dev.xkmc.l2core.serial.configval;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.function.DoubleSupplier;

public record DoubleConfigValue(String path, List<String> line) implements DoubleSupplier {

    public static DoubleConfigValue of(String file, ModConfigSpec.ConfigValue<Double> config) {
        return new DoubleConfigValue(file, config.getPath());
    }

    public static DoubleConfigValue of(String data) {
        int last = data.lastIndexOf('/');
        var line = data.substring(last + 1).split("\\.");
        return new DoubleConfigValue(data.substring(0, last), List.of(line));
    }

    public static Codec<DoubleSupplier> CODEC = Codec.either(Codec.DOUBLE, Codec.STRING)
        .xmap(e -> e.map(x -> (DoubleSupplier) (() -> x), DoubleConfigValue::of),
            e -> e instanceof DoubleConfigValue val ?
                Either.right(val.toData()) :
                Either.left(e.getAsDouble()));

    @Override
    public double getAsDouble() {
        return AbstractConfigParser.parse(path, line)
            .map(e -> e instanceof Number val ? val.doubleValue() : 0d)
            .orElse(0d);
    }

    public String toData() {
        StringBuilder lines = new StringBuilder();
        for (var e : line) {
            if (!lines.isEmpty()) {
                lines.append(".");
            }
            lines.append(e);
        }
        return path + "/" + lines;
    }

}

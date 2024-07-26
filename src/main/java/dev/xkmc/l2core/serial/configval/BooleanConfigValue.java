package dev.xkmc.l2core.serial.configval;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.xkmc.l2core.util.ConfigInit;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public record BooleanConfigValue(String path, List<String> line) implements BooleanSupplier {

	public static <T extends ConfigInit> BooleanConfigValue of(T file, Function<T, ModConfigSpec.ConfigValue<Boolean>> config) {
		return new BooleanConfigValue(file.getPath(), config.apply(file).getPath());
	}

	public static BooleanConfigValue of(String data) {
		int last = data.lastIndexOf('/');
		var line = data.substring(last + 1).split("\\.");
		return new BooleanConfigValue(data.substring(0, last), List.of(line));
	}

	public static Codec<BooleanSupplier> CODEC = Codec.either(Codec.BOOL, Codec.STRING)
			.xmap(e -> e.map(x -> (BooleanSupplier) (() -> x), BooleanConfigValue::of),
					e -> e instanceof BooleanConfigValue val ?
							Either.right(val.toData()) :
							Either.left(e.getAsBoolean()));

	public boolean getAsBoolean() {
		return AbstractConfigParser.parse(path, line)
				.map(e -> e instanceof Boolean val ? val : false)
				.orElse(false);
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

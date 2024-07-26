package dev.xkmc.l2core.serial.configval;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.xkmc.l2core.util.ConfigInit;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntSupplier;

public record IntConfigValue(String path, List<String> line) implements IntSupplier {

	public static <T extends ConfigInit> IntConfigValue of(T file, Function<T, ModConfigSpec.ConfigValue<Integer>> config) {
		return new IntConfigValue(file.getPath(), config.apply(file).getPath());
	}

	public static IntConfigValue of(String data) {
		int last = data.lastIndexOf('/');
		var line = data.substring(last + 1).split("\\.");
		return new IntConfigValue(data.substring(0, last), List.of(line));
	}

	public static Codec<IntSupplier> CODEC = Codec.either(Codec.INT, Codec.STRING)
			.xmap(e -> e.map(x -> (IntSupplier) (() -> x), IntConfigValue::of),
					e -> e instanceof IntConfigValue val ?
							Either.right(val.toData()) :
							Either.left(e.getAsInt()));

	public int getAsInt() {
		return AbstractConfigParser.parse(path, line)
				.map(e -> e instanceof Number val ? val.intValue() : 0)
				.orElse(0);
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

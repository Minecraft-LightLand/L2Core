package dev.xkmc.l2core.serial.config;

import com.google.gson.JsonElement;
import dev.xkmc.l2serial.serialization.codec.JsonCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public abstract class RecordDataProvider implements DataProvider {
	private final DataGenerator generator;
	private final HolderLookup.Provider pvd;
	private final String name;
	private final Map<String, Record> map = new HashMap<>();

	public RecordDataProvider(DataGenerator generator, HolderLookup.Provider pvd, String name) {
		this.generator = generator;
		this.pvd = pvd;
		this.name = name;
	}

	public abstract void add(BiConsumer<String, Record> map);

	public CompletableFuture<?> run(CachedOutput cache) {
		Path folder = this.generator.getPackOutput().getOutputFolder();
		this.add(this.map::put);
		List<CompletableFuture<?>> list = new ArrayList<>();
		this.map.forEach((k, v) -> {
			JsonElement elem = new JsonCodec(pvd).toJson(v);
			if (elem != null) {
				Path path = folder.resolve("data/" + k + ".json");
				list.add(DataProvider.saveStable(cache, elem, path));
			}

		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}

	public String getName() {
		return this.name;
	}

}

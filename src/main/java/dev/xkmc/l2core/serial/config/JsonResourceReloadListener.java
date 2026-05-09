package dev.xkmc.l2core.serial.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class JsonResourceReloadListener extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final FileToIdConverter lister;

	public JsonResourceReloadListener(FileToIdConverter lister) {
		this.lister = lister;
	}

	protected Map<Identifier, JsonElement> prepare(ResourceManager manager, ProfilerFiller profiler) {
		Map<Identifier, JsonElement> result = new HashMap<>();
		// Neo: add condition context
		scanDirectory(manager, this.lister, this.makeConditionalOps(JsonOps.INSTANCE), result);
		return result;
	}


	public static void scanDirectory(
			ResourceManager manager, FileToIdConverter lister, DynamicOps<JsonElement> ops, Map<Identifier, JsonElement> result
	) {
		var conditionalCodec = ConditionalOps.createConditionalCodec(Codec.PASSTHROUGH);
		for (Entry<Identifier, Resource> entry : lister.listMatchingResources(manager).entrySet()) {
			Identifier location = entry.getKey();
			Identifier id = lister.fileToId(location);

			try (Reader reader = entry.getValue().openAsReader()) {
				conditionalCodec.parse(ops, JsonParser.parseReader(reader)).ifSuccess(parsed -> {
					if (parsed.isEmpty()) {
						LOGGER.debug("Skipping loading data file '{}' from '{}' as its conditions were not met", id, location);
					} else if (result.putIfAbsent(id, parsed.get().cast(ops)) != null) {
						throw new IllegalStateException("Duplicate data file ignored with ID " + id);
					}
				}).ifError(error -> LOGGER.error("Couldn't parse data file '{}' from '{}': {}", id, location, error));
			} catch (IllegalArgumentException | IOException | JsonParseException var14) {
				LOGGER.error("Couldn't parse data file '{}' from '{}'", id, location, var14);
			}
		}
	}

}

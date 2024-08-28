package dev.xkmc.l2core.init.reg.varitem;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.Level;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class VarItemInit<T extends Item> {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().create();
	private static final ConcurrentMap<ResourceLocation, VarItemInit<?>> VAR_ITEM_TYPE = new ConcurrentHashMap<>();
	private static final String PATH = "varitem_config";

	public static <T extends Item> VarItemInit<T> setup(L2Registrate reg, ResourceLocation id, Function<ResourceLocation, T> func, VarBuilder<T> builder) {
		var ans = new VarItemInit<>(reg, id, func, builder);
		VAR_ITEM_TYPE.put(id, ans);
		return ans;
	}

	private final L2Registrate reg;
	private final ResourceLocation id;
	private final Function<ResourceLocation, T> func;
	private final VarBuilder<T> builder;
	private final Map<String, VarEntry<T>> defaults = new LinkedHashMap<>();
	private final List<String> registered = new ArrayList<>();
	private final Map<String, ItemEntry<T>> results = new ConcurrentHashMap<>();

	private VarItemInit(L2Registrate reg, ResourceLocation id, Function<ResourceLocation, T> func, VarBuilder<T> builder) {
		this.reg = reg;
		this.id = id;
		this.func = func;
		this.builder = builder;
		reg.getModEventBus().addListener( EventPriority.HIGH, RegisterEvent.class, this::init);
	}

	public synchronized void add(List<String> defaults) {
		defaults.forEach(e -> this.defaults.put(e, new SimpleVarEntry<>(e)));
	}

	public synchronized VarHolder<T> add(VarHolder<T> e) {
		defaults.put(e.id(), e);
		return e;
	}

	private synchronized void init(RegisterEvent event) {
		if (event.getRegistry() != BuiltInRegistries.ITEM) return;
		load();
		for (var e : registered) {
			var rl = ResourceLocation.fromNamespaceAndPath(reg.getModid(), e);
			if (BuiltInRegistries.ITEM.containsKey(rl)) {
				L2Core.LOGGER.error("Item ID {} is already used. Registration of varitem type {} skips this ID.", rl, id);
				continue;
			}
			L2Core.LOGGER.debug("Registering {} for varitem type {}", rl, id);
			var def = defaults.get(e);
			var b = builder.build(rl, reg.item(e, p -> func.apply(rl)));
			if (def != null) b = def.builder().build(rl, b);
			var item = b.register();
			results.put(e, item);
			if (def != null) def.callback(item);
		}
	}

	private void load() {
		String path = PATH + "/" + id.getNamespace() + "-" + id.getPath() + ".json";
		var file = FMLPaths.CONFIGDIR.get().resolve(path).toFile();
		registered.clear();
		try {
			var parent = file.getParentFile();
			if (parent.exists()) {
				if (!parent.isDirectory()) {
					if (parent.delete()) {
						parent.mkdirs();
					}
				}
			} else parent.mkdirs();
			if (!file.exists()) file.createNewFile();
			var reader = new FileReader(file);
			var json = JsonParser.parseReader(reader);
			reader.close();
			var result = parseFile(json);
			List<String> ans = result.getSecond();
			if (result.getFirst()) {
				if (ans.isEmpty()) ans = new ArrayList<>(defaults.keySet());
				var writer = new FileWriter(file);
				var out = new JsonArray();
				for (var e : ans) {
					out.add(e);
				}
				writer.write(GSON.toJson(out));
				writer.close();
			}
			registered.addAll(ans);
		} catch (Exception e) {
			L2Core.LOGGER.error("Failed to parse config file for varitem type {}. Use defaults instead", id);
			L2Core.LOGGER.throwing(Level.ERROR, e);
			registered.addAll(defaults.keySet());
		}
	}

	private Pair<Boolean, List<String>> parseFile(JsonElement elem) {
		if (!elem.isJsonArray()) return Pair.of(true, List.of());
		List<String> ans = new ArrayList<>();
		boolean err = false;
		for (var e : elem.getAsJsonArray()) {
			var rl = e.getAsString();
			if (ResourceLocation.isValidPath(rl)) ans.add(rl);
			else {
				L2Core.LOGGER.error("Item ID {} for varitem type {} is invalid. Skipped", rl, id);
				err = true;
			}
		}
		return Pair.of(err, ans);
	}

	private record SimpleVarEntry<T extends Item>(String id) implements VarEntry<T> {

		@Override
		public void callback(ItemEntry<T> item) {

		}

		@Override
		public VarBuilder<T> builder() {
			return VarBuilder.nop();
		}

	}

}

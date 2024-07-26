package dev.xkmc.l2core.util;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigInit {

	private static final ConcurrentHashMap<String, ConfigInit> MAP = new ConcurrentHashMap<>();

	@Nullable
	public static ConfigInit get(String path) {
		return MAP.get(path);
	}

	private String folder = null;
	private String path = "";

	public ModConfig.Type getType() {
		return type;
	}

	public IConfigSpec getSpec() {
		return spec;
	}

	private ModConfig.Type type;
	private IConfigSpec spec;

	public String getPath() {
		return path;
	}

	protected void folder(String str) {
		if (folder != null) throw new IllegalStateException("folder already set to " + folder);
		this.folder = str;
	}

	protected void markL2() {
		folder("l2configs/");
	}

	protected void markPlain() {
		folder("");
	}

	public static <T extends ConfigInit> T register(L2Registrate reg, ModConfig.Type type, Function<Builder, T> factory) {
		var builder = new Builder(reg);
		var ans = factory.apply(builder);
		var spec = builder.build();
		register(reg, type, spec, ans);
		return ans;
	}

	private static void register(L2Registrate reg, ModConfig.Type type, IConfigSpec spec, ConfigInit val) {
		if (val.folder == null) throw new IllegalStateException("Config must specify folder");
		var mod = ModLoadingContext.get().getActiveContainer();
		String path = val.folder + mod.getModId() + "-" + type.extension() + ".toml";
		mod.registerConfig(type, spec, path);
		val.path = path;
		val.type = type;
		val.spec = spec;
		MAP.put(path, val);
		RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> initClient(mod));
		reg.initConfigTitle(mod);
		String typeName = RegistrateLangProvider.toEnglishName(type.extension());
		String fileName = reg.getModid() + ".configuration.section." + path.replaceAll("[-_/]", ".");
		String title = mod.getModInfo().getDisplayName() + " " + typeName + " Configuration";
		reg.addRawLang(fileName, title);
		reg.addRawLang(fileName + ".title", title);
	}

	private static void initClient(ModContainer mod) {
		mod.<IConfigScreenFactory>registerExtensionPoint(IConfigScreenFactory.class, () -> ConfigurationScreen::new);
	}

	public static class Builder extends ModConfigSpec.Builder {

		private final L2Registrate reg;

		private String text = null;

		Builder(L2Registrate reg) {
			this.reg = reg;
		}

		@Deprecated
		@Override
		public Builder push(String path) {
			super.push(path);
			return this;
		}

		public Builder push(String path, String name) {
			reg.addRawLang(reg.getModid() + ".configuration." + path, name);
			super.push(path);
			return this;
		}

		public Builder text(String text) {
			this.text = text;
			return this;
		}

		@Override
		public <T> ModConfigSpec.ConfigValue<T> define(List<String> path, ModConfigSpec.ValueSpec value, Supplier<T> defaultSupplier) {
			if (text == null) throw new IllegalStateException("text not specified");
			reg.addRawLang(reg.getModid() + ".configuration." + path.getLast(), text);
			String comment = value.getComment();
			reg.addRawLang(reg.getModid() + ".configuration." + path.getLast() + ".tooltip", comment == null ? "" : comment);
			return super.define(path, value, defaultSupplier);
		}

		@Override
		public ModConfigSpec.Builder comment(String comment) {
			return super.comment(comment);
		}
	}

}

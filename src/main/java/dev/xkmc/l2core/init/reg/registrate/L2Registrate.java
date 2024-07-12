package dev.xkmc.l2core.init.reg.registrate;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.NoConfigBuilder;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;
import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2serial.serialization.custom_handler.CodecHandler;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class L2Registrate extends AbstractRegistrate<L2Registrate> {

	public final NonNullSupplier<Boolean> doDataGen = NonNullSupplier.lazy(DatagenModLoader::isRunningDataGen);

	public L2Registrate(String modid) {
		super(modid);
		var bus = ModLoadingContext.get().getActiveContainer().getEventBus();
		if (bus != null) registerEventListeners(bus);
		else L2Core.LOGGER.error("Failed to register mod {}", modid);
	}

	public ResourceLocation loc(String id) {
		return ResourceLocation.fromNamespaceAndPath(getModid(), id);
	}

	public <T, P extends T> GenericBuilder<T, P> generic(RegistryInstance<T> cls, String id, NonNullSupplier<P> sup) {
		return entry(id, cb -> new GenericBuilder<>(this, id, cb, cls.key(), sup));
	}

	public <T extends MobEffect> NoConfigBuilder<MobEffect, T, L2Registrate> effect(String name, NonNullSupplier<T> sup, String desc) {
		addRawLang("effect." + getModid() + "." + name + ".description", desc);
		addRawLang("effect." + getModid() + "." + name + ".desc", desc);
		return entry(name, cb -> new NoConfigBuilder<>(this, this, name, cb, Registries.MOB_EFFECT, sup));
	}

	public <T extends Potion> SimpleEntry<Potion> potion(String name, NonNullSupplier<T> sup) {
		RegistryEntry<Potion, T> ans = entry(name, (cb) -> new NoConfigBuilder<>(this, this, name, cb,
				Registries.POTION, sup)).register();
		if (doDataGen.get()) {
			List<Item> list = List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION, Items.TIPPED_ARROW);
			for (Item item : list) {
				String pref = item.getDescriptionId();
				String[] prefs = pref.split("\\.");
				String str = item.getDescriptionId() + ".effect." + name;
				String pref_name = RegistrateLangProvider.toEnglishName(prefs[prefs.length - 1]);
				if (item == Items.TIPPED_ARROW) pref_name = "Arrow";
				addRawLang(str, pref_name + " of " + RegistrateLangProvider.toEnglishName(name));
			}
		}
		return new SimpleEntry<>(ans);
	}

	@SuppressWarnings({"unsafe"})
	public <E> RegistryInstance<E> newRegistry(String id, Class<?> cls, Consumer<RegistryBuilder<E>> cons) {
		ResourceKey<Registry<E>> key = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(getModid(), id));
		var ans = new RegistryBuilder<>(key);
		cons.accept(ans);
		var reg = ans.create();
		new CodecHandler<>(Wrappers.cast(cls), reg.byNameCodec(), ByteBufCodecs.fromCodecWithRegistries(reg.byNameCodec()));
		OneTimeEventReceiver.addModListener(this, NewRegistryEvent.class, (e) -> e.register(reg));
		return new RegistryInstance<>(reg, key);
	}

	public <E> RegistryInstance<E> newRegistry(String id, Class<?> cls) {
		return newRegistry(id, cls, e -> {
		});
	}

	public synchronized SimpleEntry<CreativeModeTab> buildModCreativeTab(String name, String def, Consumer<CreativeModeTab.Builder> config) {
		ResourceLocation id = ResourceLocation.fromNamespaceAndPath(getModid(), name);
		defaultCreativeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, id));
		return buildCreativeTabImpl(name, addLang("itemGroup", id, def), config);
	}

	public synchronized SimpleEntry<CreativeModeTab> buildL2CreativeTab(String name, String def, Consumer<CreativeModeTab.Builder> config) {
		ResourceLocation id = ResourceLocation.fromNamespaceAndPath(L2Core.MODID, name);
		defaultCreativeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, id));
		TabSorter sorter = new TabSorter(getModid() + ":" + name, id);
		return L2Core.REGISTRATE.buildCreativeTabImpl(name, addLang("itemGroup", id, def), b -> {
			config.accept(b);
			sorter.sort(b);
		});
	}

	private synchronized SimpleEntry<CreativeModeTab> buildCreativeTabImpl(String name, Component comp, Consumer<CreativeModeTab.Builder> config) {
		return new SimpleEntry<>(this.generic(self(), name, Registries.CREATIVE_MODE_TAB, () -> {
			var builder = CreativeModeTab.builder().title(comp)
					.withTabsBefore(CreativeModeTabs.SPAWN_EGGS);
			config.accept(builder);
			return builder.build();
		}).register());
	}

	public record RegistryInstance<E>(
			Registry<E> reg,
			ResourceKey<Registry<E>> key
	) implements Supplier<Registry<E>> {

		@Override
		public Registry<E> get() {
			return reg;
		}

	}

	public static class GenericBuilder<T, P extends T> extends AbstractBuilder<T, P, L2Registrate, GenericBuilder<T, P>> {

		private final NonNullSupplier<P> sup;

		GenericBuilder(L2Registrate parent, String name, BuilderCallback callback, ResourceKey<Registry<T>> registryType, NonNullSupplier<P> sup) {
			super(parent, parent, name, callback, registryType);
			this.sup = sup;
		}

		@Override
		protected @NonnullType @NotNull P createEntry() {
			return sup.get();
		}

		public GenericBuilder<T, P> defaultLang() {
			var reg = getRegistryKey().location();
			String id = reg.getPath() + "." + getOwner().getModid() + "." + getName();
			return lang(e -> id, RegistrateLangProvider.toEnglishName(this.getName()));
		}

	}

	private static class TabSorter {

		private static final TreeMap<String, TabSorter> MAP = new TreeMap<>();
		private static final HashSet<ResourceLocation> SET = new HashSet<>();

		private final ResourceLocation id;

		private TabSorter(String str, ResourceLocation id) {
			MAP.put(str, this);
			SET.add(id);
			this.id = id;
		}

		public void sort(CreativeModeTab.Builder b) {
			var list = new ArrayList<>(MAP.values());
			boolean after = false;
			ResourceLocation before = null;
			for (var e : list) {
				if (e == this) {
					after = true;
					if (before != null) {
						b.withTabsBefore(before);
					}
					continue;
				}
				if (after) {
					b.withTabsAfter(e.id);
					return;
				} else {
					before = e.id;
				}
			}
			for (var e : BuiltInRegistries.CREATIVE_MODE_TAB.entrySet()) {
				var id = e.getKey().location();
				if (known(id) || known(e.getValue())) {
					continue;
				}
				b.withTabsAfter(id);
			}
		}

		private static boolean known(ResourceLocation id) {
			if (id.getNamespace().equals("minecraft")) {
				return true;
			}
			return SET.contains(id);
		}

		private static boolean known(CreativeModeTab tab) {
			for (var other : tab.tabsAfter) {
				if (known(other)) {
					return true;
				}
			}
			return false;
		}

	}

}
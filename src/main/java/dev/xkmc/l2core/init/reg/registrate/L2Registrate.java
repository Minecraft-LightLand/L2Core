package dev.xkmc.l2core.init.reg.registrate;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.NoConfigBuilder;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;
import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.l2core.util.ConfigInit;
import dev.xkmc.l2serial.serialization.custom_handler.CodecHandler;
import dev.xkmc.l2serial.util.ModContainerHack;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class L2Registrate extends AbstractRegistrate<L2Registrate> {

	public final NonNullSupplier<Boolean> doDataGen = NonNullSupplier.lazy(DatagenModLoader::isRunningDataGen);

	public L2Registrate(String modid) {
		super(modid);
		var mod = ModContainerHack.getMod(modid);
		var bus = mod.getEventBus();
		if (bus != null) registerEventListeners(bus);
		else L2Core.LOGGER.error("Failed to register mod {}", modid);
		addRawLang(modid + ".title", mod.getModInfo().getDisplayName());
	}

	public ResourceLocation loc(String id) {
		return ResourceLocation.fromNamespaceAndPath(getModid(), id);
	}

	private boolean initConfig = false;

	public <T extends ConfigInit> T registerClient(Function<ConfigInit.Builder, T> factory) {
		return ConfigInit.register(this, ModConfig.Type.CLIENT, factory);
	}

	public <T extends ConfigInit> T registerUnsynced(Function<ConfigInit.Builder, T> factory) {
		return ConfigInit.register(this, ModConfig.Type.COMMON, factory);
	}

	public <T extends ConfigInit> T registerSynced(Function<ConfigInit.Builder, T> factory) {
		return ConfigInit.register(this, ModConfig.Type.SERVER, factory);
	}

	public void initConfigTitle(ModContainer mod) {
		if (initConfig) return;
		initConfig = true;
		addRawLang(getModid() + ".configuration.title", mod.getModInfo().getDisplayName() + " Configuration");
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
		return potion(name, RegistrateLangProvider.toEnglishName(name), sup);
	}

	public <T extends Potion> SimpleEntry<Potion> potion(String name, String desc, NonNullSupplier<T> sup) {
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
				addRawLang(str, pref_name + " of " + desc);
			}
		}
		return new SimpleEntry<>(ans);
	}

	public <T extends ParticleOptions, R extends ParticleType<T>> Val<R>
	particle(String name, NonNullSupplier<R> sup, NonNullSupplier<? extends ParticleSupplier<T>> pvd) {
		RegistryEntry<ParticleType<?>, R> ans = entry(name, (cb) -> new NoConfigBuilder<>(this, this, name, cb,
				Registries.PARTICLE_TYPE, sup)).register();
		RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
				OneTimeEventReceiver.addModListener(this, RegisterParticleProvidersEvent.class,
						event -> pvd.get().register().accept(event, ans.get())));
		return new Val.Registrate<>(ans);
	}

	public <E> RegistryInstance<E> newRegistry(String id) {
		ResourceKey<Registry<E>> key = ResourceKey.createRegistryKey(loc(id));
		RegistryBuilder<E> ans = new RegistryBuilder<>(key);
		Registry<E> reg = ans.create();
		OneTimeEventReceiver.addModListener(this, NewRegistryEvent.class, (e) -> e.register(reg));
		return new RegistryInstance<>(reg, key);
	}

	@SuppressWarnings({"unsafe"})
	public <E> RegistryInstance<E> newRegistry(String id, Class<?> cls, Consumer<RegistryBuilder<E>> cons) {
		ResourceKey<Registry<E>> key = ResourceKey.createRegistryKey(loc(id));
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

	public interface ParticleSupplier<T extends ParticleOptions> {

		static <T extends ParticleOptions> ParticleSupplier<T> provider(NonNullSupplier<ParticleProvider<T>> pvd) {
			return () -> (event, type) -> event.registerSpecial(type, pvd.get());
		}

		static <T extends ParticleOptions> ParticleSupplier<T> sprite(NonNullSupplier<ParticleProvider.Sprite<T>> pvd) {
			return () -> (event, type) -> event.registerSprite(type, pvd.get());
		}

		static <T extends ParticleOptions> ParticleSupplier<T> spriteSet(NonNullSupplier<ParticleEngine.SpriteParticleRegistration<T>> pvd) {
			return () -> (event, type) -> event.registerSpriteSet(type, pvd.get());
		}

		BiConsumer<RegisterParticleProvidersEvent, ParticleType<T>> register();

	}

}
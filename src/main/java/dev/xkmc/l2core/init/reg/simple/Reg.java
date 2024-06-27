package dev.xkmc.l2core.init.reg.simple;

import com.mojang.serialization.Codec;
import dev.xkmc.l2core.init.reg.datapack.DataMapReg;
import dev.xkmc.l2core.init.reg.datapack.DatapackReg;
import dev.xkmc.l2serial.serialization.codec.CodecAdaptor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public final class Reg {

	private final String modid;
	private final IEventBus bus;

	public Reg(String modid) {
		this.modid = modid;

		var cont = ModLoadingContext.get().getActiveContainer();
		if (!cont.getModId().equals(modid))
			throw new IllegalStateException("Class Initialized from wrong thread for " + modid);
		var bus = cont.getEventBus();
		if (bus != null) this.bus = bus;
		else throw new IllegalStateException("Event bus is null for " + modid);
	}

	public <T> DeferredRegister<T> make(Registry<T> reg) {
		var ans = DeferredRegister.create(reg, modid);
		ans.register(bus);
		return ans;
	}

	public <T> DeferredRegister<T> make(ResourceKey<Registry<T>> reg) {
		var ans = DeferredRegister.create(reg, modid);
		ans.register(bus);
		return ans;
	}

	public <T> DatapackReg<T> dataReg(String id, Codec<T> codec) {
		var ans = new DatapackReg<>(ResourceKey.createRegistryKey(loc(id)), codec);
		bus.addListener(ans::onRegister);
		return ans;
	}

	public <T> DatapackReg<T> dataReg(String id, Class<T> cls) {
		return dataReg(id, new CodecAdaptor<>(cls));
	}

	public <K, V> DataMapReg<K, V> dataMap(DataMapType<K, V> type) {
		var ans = new DataMapReg<>(type);
		bus.addListener(ans::register);
		return ans;
	}

	public <K, V> DataMapReg<K, V> dataMap(String id, ResourceKey<Registry<K>> k, Codec<V> codec, Codec<V> network) {
		return dataMap(DataMapType.builder(loc(id), k, codec).synced(network, true).build());
	}

	public <K, V> DataMapReg<K, V> dataMap(String id, ResourceKey<Registry<K>> k, Class<V> cls) {
		CodecAdaptor<V> codec = new CodecAdaptor<>(cls);
		return dataMap(id, k, codec, codec);
	}

	public ResourceLocation loc(String id) {
		return ResourceLocation.fromNamespaceAndPath(modid, id);
	}


}

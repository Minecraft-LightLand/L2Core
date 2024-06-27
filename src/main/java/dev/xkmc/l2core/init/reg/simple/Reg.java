package dev.xkmc.l2core.init.reg.simple;

import com.mojang.serialization.Codec;
import dev.xkmc.l2core.init.reg.datapack.DataMapReg;
import dev.xkmc.l2core.init.reg.datapack.DatapackReg;
import dev.xkmc.l2serial.serialization.codec.CodecAdaptor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class Reg {

    private final String modid;

    public Reg(String modid) {
        this.modid = modid;
    }

    public <T> DeferredRegister<T> make(Registry<T> reg) {
        var ans = DeferredRegister.create(reg, modid);
        listen(ans::register);
        return ans;
    }

    public <T> DeferredRegister<T> make(ResourceKey<Registry<T>> reg) {
        var ans = DeferredRegister.create(reg, modid);
        listen(ans::register);
        return ans;
    }

    public <T> DatapackReg<T> dataReg(String id, Codec<T> codec) {
        var ans = new DatapackReg<>(ResourceKey.createRegistryKey(loc(id)), codec);
        listen(bus -> bus.addListener(ans::onRegister));
        return ans;
    }

    public <T> DatapackReg<T> dataReg(String id, Class<T> cls) {
        return dataReg(id, new CodecAdaptor<>(cls));
    }

    public <K, V> DataMapReg<K, V> dataMap(DataMapType<K, V> type) {
        var ans = new DataMapReg<>(type);
        listen(bus -> bus.addListener(ans::register));
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

    private final List<Consumer<IEventBus>> list = new ArrayList<>();
    private IEventBus bus;

    private void listen(Consumer<IEventBus> cons) {
        if (bus == null) list.add(cons);
        else cons.accept(bus);
    }

    public void register(IEventBus bus) {
        for (var e : list) e.accept(bus);
        list.clear();
        this.bus = bus;
    }

}

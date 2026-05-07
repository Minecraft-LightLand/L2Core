package dev.xkmc.l2core.init.reg.datapack;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public record DataMapReg<K, V>(DataMapType<K, V> reg) {

	public void register(final RegisterDataMapTypesEvent event) {
		event.register(reg);
	}

	@Nullable
	public V get(RegistryAccess access, Holder<K> key) {
		return key.getData(reg);
	}

	public Stream<Pair<Holder<K>, V>> getAll(RegistryAccess access) {
		var registry = access.lookupOrThrow(reg.registryKey());
		return registry.getDataMap(reg).entrySet().stream()
				.map(e -> Pair.of(registry.get(e.getKey()).orElseThrow(), e.getValue()));
	}

}

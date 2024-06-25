package dev.xkmc.l2core.init.reg.datapack;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public record DatapackReg<T>(ResourceKey<Registry<T>> key, Codec<T> codec) {

	public void onRegister(DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(key, codec, codec);
	}

	@Nullable
	public Holder<T> get(RegistryAccess access, ResourceLocation id) {
		var reg = access.registry(key);
		if (reg.isEmpty()) return null;
		return reg.get().getHolder(id).orElse(null);
	}

	public Stream<Holder<T>> getAll(RegistryAccess access) {
		var reg = access.registry(key);
		if (reg.isEmpty()) return Stream.empty();
		return reg.get().holders().map(e -> e);
	}

}

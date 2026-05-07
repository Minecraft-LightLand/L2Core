package dev.xkmc.l2core.init.reg.datapack;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public record DatapackReg<T>(ResourceKey<Registry<T>> key, Codec<T> codec) {

	public void onRegister(DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(key, codec, codec);
	}

	@Nullable
	public Holder<T> get(RegistryAccess access, Identifier id) {
		return access.holder(ResourceKey.create(key, id)).orElse(null);
	}

	public Iterable<Holder<T>> getAll(RegistryAccess access) {
		return access.holderOrThrow(key).value().asHolderIdMap();
	}

}

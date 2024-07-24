package dev.xkmc.l2core.init.reg.ench;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Optional;
import java.util.stream.Stream;

public record FakeRegistryLookup<T>(ResourceKey<? extends Registry<? extends T>> key)
		implements HolderLookup.RegistryLookup<T> {

	@Override
	public Lifecycle registryLifecycle() {
		return Lifecycle.stable();
	}

	@Override
	public Stream<Holder.Reference<T>> listElements() {
		return Stream.empty();
	}

	@Override
	public Stream<HolderSet.Named<T>> listTags() {
		return Stream.empty();
	}

	@Override
	public Optional<Holder.Reference<T>> get(ResourceKey<T> pResourceKey) {
		return Optional.empty();
	}

	@Override
	public Optional<HolderSet.Named<T>> get(TagKey<T> pTagKey) {
		return Optional.empty();
	}

	@Override
	public boolean canSerializeIn(HolderOwner<T> pOwner) {
		return true;
	}

}

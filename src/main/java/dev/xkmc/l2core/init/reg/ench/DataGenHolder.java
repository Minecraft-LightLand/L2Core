package dev.xkmc.l2core.init.reg.ench;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public record DataGenHolder<T>(ResourceKey<T> key, T val) implements Holder<T> {
	@Override
	public T value() {
		return val;
	}

	@Override
	public boolean areComponentsBound() {
		return true;
	}

	@Override
	public DataComponentMap components() {
		return DataComponentMap.EMPTY;
	}

	@Override
	public boolean isBound() {
		return true;
	}

	@Override
	public boolean is(Identifier pLocation) {
		return false;
	}

	@Override
	public boolean is(ResourceKey<T> pResourceKey) {
		return false;
	}

	@Override
	public boolean is(Predicate<ResourceKey<T>> pPredicate) {
		return false;
	}

	@Override
	public boolean is(TagKey<T> pTagKey) {
		return false;
	}

	@Override
	public boolean is(Holder<T> pHolder) {
		return false;
	}

	@Override
	public Stream<TagKey<T>> tags() {
		return Stream.empty();
	}

	@Override
	public Either<ResourceKey<T>, T> unwrap() {
		return Either.left(key());
	}

	@Override
	public Optional<ResourceKey<T>> unwrapKey() {
		return Optional.of(key);
	}

	@Override
	public Kind kind() {
		return Kind.REFERENCE;
	}

	@Override
	public boolean canSerializeIn(HolderOwner<T> pOwner) {
		return true;
	}
}

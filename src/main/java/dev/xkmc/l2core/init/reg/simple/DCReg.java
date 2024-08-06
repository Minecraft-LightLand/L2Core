package dev.xkmc.l2core.init.reg.simple;

import com.mojang.serialization.Codec;
import dev.xkmc.l2core.util.DCStack;
import dev.xkmc.l2serial.serialization.codec.CodecAdaptor;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record DCReg(DeferredRegister<DataComponentType<?>> reg) {

	public static DCReg of(Reg parent) {
		return new DCReg(parent.make(BuiltInRegistries.DATA_COMPONENT_TYPE));
	}

	public <T> DCVal<T> reg(String id, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> stream, boolean cache) {
		var builder = DataComponentType.<T>builder().persistent(codec).networkSynchronized(stream);
		if (cache) builder.cacheEncoding();
		return new DCValImpl<>(reg.register(id, builder::build));
	}

	public <T> DCVal<List<T>> list(String id, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> stream, boolean cache) {
		var builder = DataComponentType.<List<T>>builder()
				.persistent(codec.listOf())
				.networkSynchronized(stream.apply(Wrappers.cast(ByteBufCodecs.list())));
		if (cache) builder.cacheEncoding();
		return new DCValImpl<>(reg.register(id, builder::build));
	}

	public <T> DCVal<T> reg(String id, Class<T> cls, boolean cache) {
		var cdc = new CodecAdaptor<>(cls);
		return reg(id, cdc, cdc.toNetwork(), cache);
	}

	public <T> DCVal<List<T>> list(String id, Class<T> cls, boolean cache) {
		var cdc = new CodecAdaptor<>(cls);
		return reg(id, cdc.listOf(), ByteBufCodecs.<RegistryFriendlyByteBuf, T>list().apply(cdc.toNetwork()), cache);
	}

	public DCVal<Unit> unit(String id) {
		return reg(id, Unit.CODEC, StreamCodec.unit(Unit.INSTANCE), false);
	}

	public DCVal<Integer> intVal(String id) {
		return reg(id, Codec.INT, ByteBufCodecs.INT, false);
	}

	public DCVal<Double> doubleVal(String id) {
		return reg(id, Codec.DOUBLE, ByteBufCodecs.DOUBLE, false);
	}

	public DCVal<Float> floatVal(String id) {
		return reg(id, Codec.FLOAT, ByteBufCodecs.FLOAT, false);
	}

	public DCVal<Long> longVal(String id) {
		return reg(id, Codec.LONG, ByteBufCodecs.VAR_LONG, false);
	}

	public DCVal<String> str(String id) {
		return reg(id, Codec.STRING, ByteBufCodecs.STRING_UTF8, false);
	}

	public <T> DCVal<T> enumVal(String id, EnumCodec<T> codec) {
		return reg(id, codec.codec(), codec.stream(), true);
	}

	public DCVal<ResourceLocation> loc(String id) {
		return reg(id, ResourceLocation.CODEC, ResourceLocation.STREAM_CODEC, false);
	}

	public DCVal<UUID> uuid(String id) {
		return reg(id, UUIDUtil.CODEC, UUIDUtil.STREAM_CODEC, true);
	}

	public DCVal<Set<UUID>> uuidSet(String id) {
		return reg(id, UUIDUtil.CODEC_LINKED_SET, UUIDUtil.STREAM_CODEC.apply(
				ByteBufCodecs.collection(LinkedHashSet::newLinkedHashSet)), true);
	}

	public DCVal<DCStack> stack(String id) {
		return reg(id, ItemStack.OPTIONAL_CODEC.xmap(DCStack::new, DCStack::stack),
				ItemStack.OPTIONAL_STREAM_CODEC.map(DCStack::new, DCStack::stack), true);
	}

	public DCVal<Component> component(String id) {
		return reg(id, ComponentSerialization.CODEC, ComponentSerialization.STREAM_CODEC, true);
	}

	private record DCValImpl<T>(DeferredHolder<DataComponentType<?>, DataComponentType<T>> val) implements DCVal<T> {

		@Override
		public DataComponentType<T> get() {
			return val.get();
		}

		@Override
		public ResourceLocation id() {
			return val.getId();
		}

	}

}

package dev.xkmc.l2core.init.reg.simple;

import com.mojang.serialization.Codec;
import dev.xkmc.l2serial.serialization.codec.CodecAdaptor;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;

public record DCReg(DeferredRegister<DataComponentType<?>> reg) {

	public static DCReg of(Reg parent) {
		return new DCReg(parent.make(BuiltInRegistries.DATA_COMPONENT_TYPE));
	}

	public <T> DCVal<T> reg(String id, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> stream) {
		return new DCValImpl<>(reg.register(id, () -> DataComponentType.<T>builder().persistent(codec).networkSynchronized(stream).build()));
	}

	public <T> DCVal<T> reg(String id, Class<T> cls) {
		var cdc = new CodecAdaptor<>(cls);
		return new DCValImpl<>(reg.register(id, () -> DataComponentType.<T>builder().persistent(cdc).networkSynchronized(cdc.toNetwork()).build()));
	}

	public DCVal<Integer> intVal(String id) {
		return reg(id, Codec.INT, ByteBufCodecs.INT);
	}

	public DCVal<Double> doubleVal(String id) {
		return reg(id, Codec.DOUBLE, ByteBufCodecs.DOUBLE);
	}

	public DCVal<Long> longVal(String id) {
		return reg(id, Codec.LONG, ByteBufCodecs.VAR_LONG);
	}

	public DCVal<String> str(String id) {
		return reg(id, Codec.STRING, ByteBufCodecs.STRING_UTF8);
	}

	public DCVal<UUID> uuid(String id) {
		return reg(id, Codec.STRING.xmap(UUID::fromString, UUID::toString),
				StreamCodec.of((b, e) -> FriendlyByteBuf.writeUUID(b, e), b -> FriendlyByteBuf.readUUID(b)));
	}

	public DCVal<Component> component(String id) {
		return reg(id, ComponentSerialization.CODEC, ComponentSerialization.STREAM_CODEC);
	}

	private record DCValImpl<T>(DeferredHolder<DataComponentType<?>, DataComponentType<T>> val) implements DCVal<T> {

		@Override
		public DataComponentType<T> get() {
			return val.get();
		}
	}

}

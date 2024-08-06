package dev.xkmc.l2core.init.reg.simple;

import com.mojang.serialization.Codec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record EnumCodec<T>(Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> stream) {

	public static <T extends Enum<T>> EnumCodec<T> of(Class<T> cls, T[] vals) {
		var codec = Codec.STRING.xmap((e) -> {
			try {
				return Enum.valueOf(cls, e);
			} catch (Exception var4) {
				throw new IllegalArgumentException(e + " is not a valid " + cls.getSimpleName() + ". Valid values are: " + List.of(vals));
			}
		}, Enum::name);
		var stream = ByteBufCodecs.INT.map(e -> vals[e], Enum::ordinal);
		return new EnumCodec<>(codec, stream);
	}

	public EnumCodec<List<T>> toList() {
		return new EnumCodec<>(codec.listOf(), stream.apply(Wrappers.cast(ByteBufCodecs.list())));
	}

}

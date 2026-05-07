package dev.xkmc.l2core.serial.recipe;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2serial.serialization.codec.CodecAdaptor;
import dev.xkmc.l2serial.serialization.codec.MapCodecAdaptor;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class RecSerializer<R extends Recipe<I>, I extends RecipeInput> {

	public final Class<R> cls;
	private final MapCodecAdaptor<R> codec;
	private final StreamCodec<RegistryFriendlyByteBuf, R> stream;
	private final RecipeSerializer<R> serializer;

	public RecSerializer(Class<R> cls) {
		this.cls = cls;
		this.codec = MapCodecAdaptor.of(cls);
		this.stream = new CodecAdaptor<>(cls).toNetwork();
		this.serializer = new RecipeSerializer<>(codec, stream);
	}

	public RecipeSerializer<R> serializer() {
		return serializer;
	}

	public MapCodec<R> codec() {
		return codec;
	}

	public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
		return stream;
	}

	@SuppressWarnings("ConstantConditions")
	public R blank() {
		return Wrappers.get(() -> cls.getConstructor().newInstance());
	}

}

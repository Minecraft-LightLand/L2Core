package dev.xkmc.l2core.init.reg.syncreg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.xkmc.l2core.init.L2Core;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.TreeMap;

public record RegistryConfigDataPayload(Map<ResourceLocation, JsonElement> map) implements CustomPacketPayload {

	private static final Gson GSON = new GsonBuilder().setLenient().create();

	public static final CustomPacketPayload.Type<RegistryConfigDataPayload> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(L2Core.MODID, "registry_config_data"));
	public static final StreamCodec<ByteBuf, RegistryConfigDataPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.map(i -> new TreeMap<>(), ResourceLocation.STREAM_CODEC,
					ByteBufCodecs.stringUtf8(0xffffff).map(JsonParser::parseString, GSON::toJson)),
			RegistryConfigDataPayload::map,
			RegistryConfigDataPayload::new);

	@Override
	public CustomPacketPayload.Type<RegistryConfigDataPayload> type() {
		return TYPE;
	}

}

package dev.xkmc.l2core.init.reg.syncreg;

import dev.xkmc.l2core.init.L2Core;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public final class RegistryConfigAckPayload implements CustomPacketPayload {
	public static final Type<RegistryConfigAckPayload> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(L2Core.MODID, "registry_config_ack"));
	public static final RegistryConfigAckPayload INSTANCE = new RegistryConfigAckPayload();
	public static final StreamCodec<ByteBuf, RegistryConfigAckPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	private RegistryConfigAckPayload() {
	}

	@Override
	public Type<RegistryConfigAckPayload> type() {
		return TYPE;
	}
}

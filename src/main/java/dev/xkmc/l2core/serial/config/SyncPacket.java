package dev.xkmc.l2core.serial.config;

import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;

public record SyncPacket(String id, ArrayList<ConfigData> map)
		implements SerialPacketBase<SyncPacket> {

	public static SyncPacket of(String modid, ArrayList<PacketHandlerWithConfig.ConfigInstance> configs, PlayerList playerList) {
		var access = ServerLifecycleHooks.getCurrentServer().registryAccess();
		ArrayList<ConfigData> data = new ArrayList<>();
		for (var e : configs) {
			var buf = PacketCodec.toBytes(access, e.config(), BaseConfig.class, x -> true);
			data.add(new ConfigData(e.name(), e.id(), buf.length, buf));
		}
		return new SyncPacket(modid, data);
	}

	@Override
	public void handle(Player player) {
		if (map != null) {
			var handler = PacketHandlerWithConfig.INTERNAL.get(id);
			ArrayList<PacketHandlerWithConfig.ConfigInstance> data = new ArrayList<>();
			var access = player.registryAccess();
			for (var e : map) {
				if (e.len != e.config.length) {
					L2Core.LOGGER.info("Corrupted config {name = {}:{}, id = {}, length = {}, actual length = {}}", id, e.name(), e.id, e.len, e.config.length);
					continue;
				}
				try {
					BaseConfig config = PacketCodec.fromBytes(access, e.config, BaseConfig.class, null, x -> true);
					if (config != null) {
						data.add(new PacketHandlerWithConfig.ConfigInstance(e.name, e.id, config));
						L2Core.LOGGER.debug("Successfully read config {name = {}:{}, id = {}, length = {}}", id, e.name(), e.id, e.len);
					} else {
						L2Core.LOGGER.error("Failed to read config {name = {}:{}, id = {}, length = {}}", id, e.name(), e.id, e.len);
					}
				} catch (Throwable err) {
					L2Core.LOGGER.throwing(err);
				}
			}
			handler.listener.apply(data);
		}
	}


	public record ConfigData(String name, ResourceLocation id, int len, byte[] config) {

	}

}

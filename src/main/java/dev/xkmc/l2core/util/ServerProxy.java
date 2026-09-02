package dev.xkmc.l2core.util;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Optional;

public class ServerProxy {

	@Nullable
	public static RegistryAccess getRegistryAccess() {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			Level level = Proxy.getLevel();
			if (level != null) {
				return level.registryAccess();
			}
		}
		var server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) {
			return server.registryAccess();
		}
		return null;
	}

	public static Optional<MinecraftServer> getServer() {
		return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer());
	}

	public static boolean isOnClient() {
		if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) return false;
		return Proxy.isOnClient();
	}

	public static boolean hasClientLevel() {
		if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) return false;
		return Proxy.isOnClient() && Proxy.getLevel() != null;
	}

}

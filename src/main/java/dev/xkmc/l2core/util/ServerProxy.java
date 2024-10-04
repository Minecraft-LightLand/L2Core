package dev.xkmc.l2core.util;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;

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

}

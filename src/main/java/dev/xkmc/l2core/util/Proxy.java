package dev.xkmc.l2core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Optional;

public class Proxy {

	public static RegistryAccess getRegistryAccess() {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			return Minecraft.getInstance().level.registryAccess();
		}
		return ServerLifecycleHooks.getCurrentServer().registryAccess();
	}

	public static Optional<MinecraftServer> getServer() {
		return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer());
	}

	@Nullable
	public static LocalPlayer getClientPlayer() {
		return Minecraft.getInstance().player;
	}

}

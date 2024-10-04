package dev.xkmc.l2core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Optional;

public class Proxy {

	@Nullable
	public static Player getPlayer() {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			return Minecraft.getInstance().player;
		}
		return null;
	}

	@Nullable
	public static Level getLevel() {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			return Minecraft.getInstance().level;
		}
		var server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) {
			return server.overworld();
		}
		return null;
	}

	public static Optional<MinecraftServer> getServer() {
		return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer());
	}

	@Nullable
	public static LocalPlayer getClientPlayer() {
		return Minecraft.getInstance().player;
	}

}

package dev.xkmc.l2core.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class Proxy {

	@Nullable
	public static Player getPlayer() {
		if (FMLEnvironment.getDist() == Dist.CLIENT) {
			return Minecraft.getInstance().player;
		}
		return null;
	}

	@Nullable
	public static Level getLevel() {
		if (FMLEnvironment.getDist() == Dist.CLIENT) {
			return Minecraft.getInstance().level;
		}
		var server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) {
			return server.overworld();
		}
		return null;
	}

	@Deprecated
	public static Optional<MinecraftServer> getServer() {
		return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer());
	}

	@Nullable
	public static LocalPlayer getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	static boolean isOnClient() {
		return getServer().isEmpty() || RenderSystem.isOnRenderThread();
	}

}

package dev.xkmc.l2core.init.reg.syncreg;

import com.google.gson.JsonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.*;

public class SyncRegistryConfigClientHandler {

	public static synchronized void handleData(RegistryConfigDataPayload payload, IPayloadContext context) {
		Set<ResourceLocation> clientMissing = new TreeSet<>();
		Set<ResourceLocation> serverMissing = new TreeSet<>();
		for (var key : RegistryConfigHandlers.MAP.keySet()) {
			if (!payload.map().containsKey(key)) {
				serverMissing.add(key);
			}
		}
		for (var key : payload.map().keySet()) {
			if (!RegistryConfigHandlers.MAP.containsKey(key)) {
				clientMissing.add(key);
			}
		}
		if (!clientMissing.isEmpty() || !serverMissing.isEmpty()) {
			context.disconnect(Component.literal("Mismatch in registry config handler definition"));//TODO info
			return;
		}
		Map<RegistryConfigHandler, JsonElement> mismatched = new LinkedHashMap<>();
		Map<ResourceLocation, Component> reasons = new TreeMap<>();
		for (var ent : payload.map().entrySet()) {
			try {
				var handler = RegistryConfigHandlers.MAP.get(ent.getKey());
				if (handler == null) continue;
				var reason = handler.verifyConfig(ent.getValue());
				if (reason != null) {
					mismatched.put(handler, ent.getValue());
					reasons.put(ent.getKey(), reason);
				}
			} catch (Exception e) {
				context.disconnect(Component.literal("Exception in decoding " + ent.getKey()));//TODO info
				return;
			}
		}
		if (!mismatched.isEmpty()) {
			context.disconnect(Component.literal("Mismatched registry config"));//TODO info
			Minecraft.getInstance().setScreen(new RegistryConfigMismatchScreen(
					new JoinMultiplayerScreen(new TitleScreen()),
					Component.literal("Mismatched Registry config"),//TODO
					() -> applyAndRestart(mismatched), reasons));
			return;
		}
		context.reply(RegistryConfigAckPayload.INSTANCE);
	}

	public static synchronized void applyAndRestart(Map<RegistryConfigHandler, JsonElement> mismatched) {
		for (var ent : mismatched.entrySet()) {
			ent.getKey().applyConfig(ent.getValue());
		}
		Minecraft.getInstance().stop();//TODO try if we can restart?
	}

}

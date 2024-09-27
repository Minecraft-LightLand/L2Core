package dev.xkmc.l2core.init.reg.syncreg;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegistryConfigHandlers {

	public static final Map<ResourceLocation, RegistryConfigHandler> MAP = new ConcurrentHashMap<>();

}

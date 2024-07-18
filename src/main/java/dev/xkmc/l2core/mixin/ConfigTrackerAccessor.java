package dev.xkmc.l2core.mixin;

import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.ConcurrentHashMap;

@Mixin(ConfigTracker.class)
public interface ConfigTrackerAccessor {

	@Accessor
	public ConcurrentHashMap<String, ModConfig> getFileMap();

}

package dev.xkmc.l2core.content.glow;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.Nullable;

public class ClientGlowingHandler {

	public static boolean isGlowing(Entity e) {
		return NeoForge.EVENT_BUS.post(new EntityGlowOverrideEvent(e)).isEnabled();
	}

	@Nullable
	public static Integer getColor(Entity e) {
		return NeoForge.EVENT_BUS.post(new EntityGlowOverrideEvent(e)).getColor();
	}

}

package dev.xkmc.l2core.serial.advancements;

import net.minecraft.advancements.Advancement;

public interface IAdvBuilder {

	default void modify(String id, Advancement.Builder builder) {

	}

	default void onBuild() {
	}

}

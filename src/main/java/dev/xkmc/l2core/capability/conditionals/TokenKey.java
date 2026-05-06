package dev.xkmc.l2core.capability.conditionals;

import net.minecraft.resources.Identifier;

public record TokenKey<T extends ConditionalToken>(String type, String id) {

	public static <T extends ConditionalToken> TokenKey<T> of(Identifier id) {
		return new TokenKey<>(id.getNamespace(), id.getPath());
	}

	public Identifier asLocation() {
		return Identifier.fromNamespaceAndPath(type, id);
	}

}

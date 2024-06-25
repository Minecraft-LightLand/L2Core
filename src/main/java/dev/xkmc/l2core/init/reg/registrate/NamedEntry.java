package dev.xkmc.l2core.init.reg.registrate;

import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class NamedEntry<T extends NamedEntry<T>> {

	private final L2Registrate.RegistryInstance<T> registry;

	private String desc = null;
	private ResourceLocation id = null;

	public NamedEntry(L2Registrate.RegistryInstance<T> registry) {
		this.registry = registry;
	}

	public String getDescriptionId() {
		if (desc != null)
			return desc;
		ResourceLocation rl = getRegistryName();
		ResourceLocation reg = registry.key().location();
		desc = reg.getPath() + "." + rl.getNamespace() + "." + rl.getPath();
		return desc;
	}

	public MutableComponent getDesc() {
		return Component.translatable(getDescriptionId());
	}

	public ResourceLocation getRegistryName() {
		if (id != null) return id;
		id = registry.get().getKey(getThis());
		if (id == null) {
			throw new IllegalStateException("Entry %s is not registered".formatted(getClass().getSimpleName()));
		}
		return id;
	}

	public String getID() {
		return getRegistryName().toString();
	}

	public T getThis() {
		return Wrappers.cast(this);
	}

}

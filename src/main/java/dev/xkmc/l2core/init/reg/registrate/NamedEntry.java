package dev.xkmc.l2core.init.reg.registrate;

/*TODO
public class NamedEntry<T extends NamedEntry<T>> {

	private final L2Registrate.RegistryInstance<T> registry;

	private String desc = null;

	public NamedEntry(L2Registrate.RegistryInstance<T> registry) {
		this.registry = registry;
	}

	public @NotNull String getDescriptionId() {
		if (desc != null)
			return desc;
		ResourceLocation rl = getRegistryName();
		ResourceLocation reg = registry.get().getRegistryName();
		desc = reg.getPath() + "." + rl.getNamespace() + "." + rl.getPath();
		return desc;
	}

	public MutableComponent getDesc() {
		return Component.translatable(getDescriptionId());
	}

	public ResourceLocation getRegistryName() {
		return Objects.requireNonNull(registry.get().getKey(getThis()));
	}

	public String getID() {
		return getRegistryName().toString();
	}

	public T getThis() {
		return Wrappers.cast(this);
	}

}
*/

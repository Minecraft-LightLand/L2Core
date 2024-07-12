package dev.xkmc.l2core.capability.attachment;

import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * only entities will be automatically attached by default for efficiency
 */
public class GeneralCapabilityHolder<E extends IAttachmentHolder, T extends GeneralCapabilityTemplate<E, T>> extends AttachmentDef<T> {

	public static final Map<ResourceLocation, GeneralCapabilityHolder<?, ?>> INTERNAL_MAP = new ConcurrentHashMap<>();

	public final ResourceLocation id;
	public final Class<E> entity_class;
	private final Predicate<E> pred;


	public GeneralCapabilityHolder(ResourceLocation id, Class<T> holder_class, Supplier<T> sup,
								   Class<E> entity_class, Predicate<E> pred) {
		super(holder_class, sup);
		this.id = id;
		this.entity_class = entity_class;
		this.pred = pred;
		INTERNAL_MAP.put(id, this);
	}

	public T getOrCreate(E e) {
		return e.getData(type());
	}

	public Optional<T> getExisting(E e) {
		return e.getExistingData(type());
	}

	public boolean isFor(IAttachmentHolder holder) {
		return entity_class.isInstance(holder) && isProper(Wrappers.cast(holder));
	}

	public boolean isProper(E entity) {
		return pred.test(entity);
	}

}

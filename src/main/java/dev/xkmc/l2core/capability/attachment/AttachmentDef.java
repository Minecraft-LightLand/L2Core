package dev.xkmc.l2core.capability.attachment;

import dev.xkmc.l2serial.serialization.codec.MapCodecAdaptor;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.function.Supplier;

public class AttachmentDef<E> {
	private final Class<E> cls;
	private final Supplier<E> sup;
	private AttachmentType<E> type;

	public AttachmentDef(Class<E> cls, Supplier<E> sup) {
		this.cls = cls;
		this.sup = sup;
	}

	public AttachmentType<E> type() {
		if (type != null) return type;
		var builder = AttachmentType.builder(sup);
		builder.serialize(MapCodecAdaptor.of(cls));
		if (copyOnDeath())
			builder.copyOnDeath();
		type = builder.build();
		return type;
	}

	protected boolean copyOnDeath() {
		return false;
	}

	public Class<E> cls() {
		return cls;
	}

	public boolean isFor(IAttachmentHolder holder) {
		return true;
	}

}

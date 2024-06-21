package dev.xkmc.l2core.capability.attachment;

import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public class AttachmentDef<E> implements IAttachmentSerializer<CompoundTag, E> {
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
		builder.serialize(this);
		if (copyOnDeath())
			builder.copyOnDeath();
		type = builder.build();
		return type;
	}

	protected boolean copyOnDeath() {
		return false;
	}

	@Override
	public E read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
		return Objects.requireNonNull(Wrappers.get(() -> new TagCodec(provider).fromTag(tag, cls, null)));
	}

	@Override
	public CompoundTag write(E attachment, HolderLookup.Provider provider) {
		return Objects.requireNonNull(new TagCodec(provider).toTag(new CompoundTag(), attachment));
	}

	public Class<E> cls() {
		return cls;
	}

	public boolean isFor(IAttachmentHolder holder) {
		return true;
	}

}

package dev.xkmc.l2core.capability.level;

import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

@SerialClass
public class BaseSavedData extends SavedData {

	@Override
	public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
		new TagCodec(provider).toTag(tag, this);
		return tag;
	}

	@Override
	public boolean isDirty() {
		return true;
	}


}

package dev.xkmc.l2core.capability.level;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.world.level.saveddata.SavedData;

@SerialClass
public class BaseSavedData<T> extends SavedData {

	public final Class<T> cls;

	public BaseSavedData(Class<T> cls) {
		this.cls = cls;
	}

	@Override
	public boolean isDirty() {
		return true;
	}

}

package dev.xkmc.l2core.base.entity;

import com.mojang.serialization.Codec;
import dev.xkmc.l2core.init.L2LibReg;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SyncedData {

	public static final Serializer<Integer> INT;
	public static final Serializer<BlockPos> BLOCK_POS;
	public static final Serializer<String> STRING;
	public static final Serializer<Optional<Vec3>> VEC3;
	public static final Serializer<Optional<UUID>> UUID;

	static {
		INT = new Simple<>(EntityDataSerializers.INT, Codec.INT);
		BLOCK_POS = new Simple<>(EntityDataSerializers.BLOCK_POS, BlockPos.CODEC);
		STRING = new Simple<>(EntityDataSerializers.STRING, Codec.STRING);
		VEC3 = new Opt<>(L2LibReg.EDS_VEC3.get(), Vec3.CODEC);
		UUID = new Opt<>(L2LibReg.EDS_UUID.get(), UUIDUtil.CODEC);
	}

	private final Definer cls;

	private final List<Data<?>> list = new ArrayList<>();

	@Nullable
	private final SyncedData parent;

	public SyncedData(Definer cls) {
		this.cls = cls;
		parent = null;
	}

	public SyncedData(Definer cls, SyncedData parent) {
		this.cls = cls;
		this.parent = parent;
	}

	public void register(SynchedEntityData.Builder data) {
		for (Data<?> entry : list) {
			entry.register(data);
		}
		if (parent != null)
			parent.register(data);
	}

	public <T> EntityDataAccessor<T> define(Serializer<T> ser, T init, @Nullable String name) {
		Data<T> data = new Data<>(ser, init, name);
		list.add(data);
		return data.data;
	}

	public void write(ValueOutput tag, SynchedEntityData data) {
		for (Data<?> entry : list) {
			entry.write(tag, data);
		}
		if (parent != null)
			parent.write(tag, data);
	}

	public void read(ValueInput tag, SynchedEntityData data) {
		for (Data<?> entry : list) {
			entry.read(tag, data);
		}
		if (parent != null)
			parent.read(tag, data);
	}

	private class Data<T> {

		private final Serializer<T> ser;
		private final EntityDataAccessor<T> data;
		private final T init;
		private final @Nullable String name;

		private Data(Serializer<T> ser, T init, @Nullable String name) {
			this.ser = ser;
			this.data = cls.define(ser.data());
			this.init = init;
			this.name = name;
		}

		private void register(SynchedEntityData.Builder data) {
			data.define(this.data, this.init);
		}

		public void write(ValueOutput tag, SynchedEntityData entityData) {
			if (name == null) return;
			ser.write(tag, name, entityData.get(data));
		}

		public void read(ValueInput tag, SynchedEntityData entityData) {
			if (name == null) return;
			entityData.set(data, Optional.ofNullable(ser.read(tag, name)).orElse(init));
		}
	}

	public interface Serializer<T> {

		EntityDataSerializer<T> data();

		void write(ValueOutput pvd, String id, T t);

		@Nullable
		T read(ValueInput pvd, String id);

	}

	public record Simple<T>(EntityDataSerializer<T> data, Codec<T> codec) implements Serializer<T> {

		public void write(ValueOutput pvd, String id, T t) {
			pvd.store(id, codec, t);
		}

		@Nullable
		public T read(ValueInput pvd, String id) {
			return pvd.read(id, codec).orElse(null);
		}

	}

	public record Opt<T>(EntityDataSerializer<Optional<T>> data, Codec<T> codec) implements Serializer<Optional<T>> {

		public void write(ValueOutput pvd, String id, Optional<T> t) {
			t.ifPresent(e -> pvd.store(id, codec, e));
		}

		public Optional<T> read(ValueInput pvd, String id) {
			return pvd.read(id, codec);
		}

	}

	public interface Definer {

		<T> EntityDataAccessor<T> define(EntityDataSerializer<T> ser);

	}

}

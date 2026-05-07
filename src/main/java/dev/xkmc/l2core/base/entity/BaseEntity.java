package dev.xkmc.l2core.base.entity;

import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

@SerialClass
public abstract class BaseEntity extends Entity implements IEntityWithComplexSpawn {

	public BaseEntity(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		var dat = new TagCodec(registryAccess()).toTag(new CompoundTag(), this);
		if (dat != null) output.store("auto-serial", CompoundTag.CODEC, dat);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		var dat = input.read("auto-serial", CompoundTag.CODEC);
		if (dat.isEmpty()) return;
		new TagCodec(registryAccess()).fromTag(dat.get(), this.getClass(), this);
	}

	@Override
	public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
		PacketCodec.to(buffer, this);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void readSpawnData(RegistryFriendlyByteBuf data) {
		PacketCodec.from(data, (Class) this.getClass(), this);
	}

}

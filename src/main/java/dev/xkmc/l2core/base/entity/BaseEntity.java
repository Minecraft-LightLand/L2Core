package dev.xkmc.l2core.base.entity;

import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

import javax.annotation.ParametersAreNonnullByDefault;

@SerialClass
public abstract class BaseEntity extends Entity implements IEntityWithComplexSpawn {

	public BaseEntity(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		var dat = new TagCodec(registryAccess()).toTag(new CompoundTag(), this);
		if (dat != null) tag.put("auto-serial", dat);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		if (!tag.contains("auto-serial")) return;
		new TagCodec(registryAccess()).fromTag(tag.getCompound("auto-serial"), this.getClass(), this);
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

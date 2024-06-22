package dev.xkmc.l2core.base.tile;

import dev.xkmc.l2core.util.ServerOnly;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SerialClass
public class BaseBlockEntity extends BlockEntity {

	public BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider pvd) {
		super.loadAdditional(tag, pvd);
		if (tag.contains("auto-serial"))
			new TagCodec(pvd).fromTag(tag.getCompound("auto-serial"), getClass(), this);
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider pvd) {
		super.saveAdditional(tag, pvd);
		CompoundTag ser = new TagCodec(pvd).toTag(new CompoundTag(), getClass(), this);
		if (ser != null) tag.put("auto-serial", ser);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@ServerOnly
	public void sync() {
		if (level != null) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
		}
	}

	/**
	 * Generate data packet from server to client, called from getUpdatePacket()
	 */
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider pvd) {
		CompoundTag ans = super.getUpdateTag(pvd);
		CompoundTag ser = new TagCodec(pvd).pred(SerialField::toClient)
				.toTag(new CompoundTag(), getClass(), this);
		if (ser != null) ans.put("auto-serial", ser);
		return ans;
	}

}

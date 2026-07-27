package dev.xkmc.l2core.base.tile;

import dev.xkmc.l2core.util.ServerOnly;
import dev.xkmc.l2serial.serialization.codec.CodecAdaptor;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NullMarked;

@NullMarked
@SerialClass
public class BaseBlockEntity extends BlockEntity {

	public BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		input.read("auto-serial", CodecAdaptor.toRead(this));
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.store("auto-serial", new CodecAdaptor<>(Wrappers.cast(getClass())), this);
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

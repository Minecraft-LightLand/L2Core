package dev.xkmc.l2core.base.tile;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import dev.xkmc.l2core.util.ServerOnly;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SerialClass
public class BaseBlockEntity extends BlockEntity {

	public BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		var opt = input.read("auto-serial", CompoundTag.CODEC);
		if (opt.isPresent())
			new TagCodec(input.lookup()).fromTag(opt.get(), getClass(), this);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		if (level == null) return;
		CompoundTag ser = new TagCodec(level.registryAccess()).toTag(new CompoundTag(), getClass(), this);
		if (ser != null) output.store("auto-serial", CompoundTag.CODEC, ser);
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

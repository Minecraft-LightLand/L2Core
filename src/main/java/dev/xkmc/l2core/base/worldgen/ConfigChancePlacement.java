
package dev.xkmc.l2core.base.worldgen;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.serial.configval.DoubleConfigValue;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement;

import java.util.function.DoubleSupplier;

public class ConfigChancePlacement extends RepeatingPlacement {
	public static final MapCodec<ConfigChancePlacement> CODEC = DoubleConfigValue.CODEC.fieldOf("chance").xmap(ConfigChancePlacement::new, e -> e.chance);
	private final DoubleSupplier chance;

	private ConfigChancePlacement(DoubleSupplier chance) {
		this.chance = chance;
	}

	public static ConfigChancePlacement of(DoubleSupplier count) {
		return new ConfigChancePlacement(count);
	}

	@Override
	protected int count(RandomSource random, BlockPos pos) {
		double val = chance.getAsDouble();
		int ans = (int) val;
		return ans + (random.nextFloat() < val - ans ? 1 : 0);
	}

	@Override
	public PlacementModifierType<?> type() {
		return L2LibReg.PM_CHANCE.get();
	}
}

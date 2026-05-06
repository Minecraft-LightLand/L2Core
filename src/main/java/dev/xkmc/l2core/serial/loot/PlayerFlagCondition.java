package dev.xkmc.l2core.serial.loot;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2core.init.L2LibReg;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record PlayerFlagCondition(String flag) implements LootItemCondition {

	@Override
	public MapCodec<? extends LootItemCondition> codec() {
		return L2LibReg.LIC_FLAG.get();
	}

	@Override
	public boolean test(LootContext ctx) {
		if (!ctx.hasParameter(LootContextParams.LAST_DAMAGE_PLAYER)) return false;
		var player = ctx.getParameter(LootContextParams.LAST_DAMAGE_PLAYER);
		return L2LibReg.FLAGS.type().getExisting(player).map(e -> e.hasFlag(flag)).orElse(false);
	}

}

package dev.xkmc.l2core.serial.loot;

import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

@SerialClass
public class PlayerFlagCondition implements LootItemCondition {

    @SerialField
    public String flag;

    @Deprecated
    public PlayerFlagCondition() {

    }

    public PlayerFlagCondition(String flag) {
        this.flag = flag;
    }

    @Override
    public LootItemConditionType getType() {
        return L2LibReg.LIC_FLAG.get();
    }

    @Override
    public boolean test(LootContext ctx) {
        if (!ctx.hasParam(LootContextParams.LAST_DAMAGE_PLAYER)) return false;
        var player = ctx.getParam(LootContextParams.LAST_DAMAGE_PLAYER);
        return L2LibReg.FLAGS.type().getExisting(player).map(e -> e.hasFlag(flag)).orElse(false);
    }

}

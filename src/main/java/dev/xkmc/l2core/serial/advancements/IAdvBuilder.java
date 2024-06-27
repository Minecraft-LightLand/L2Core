package dev.xkmc.l2core.serial.advancements;

import net.minecraft.advancements.Advancement;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.List;

public interface IAdvBuilder {

    void onBuild(String id, Advancement.Builder builder, List<ICondition> conditions);

}

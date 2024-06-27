package dev.xkmc.l2core.serial.advancements;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;

import java.util.Optional;

@SerialClass
public class BaseCriterionInstance<T extends BaseCriterionInstance<T, R>, R extends BaseCriterion<T, R>>
		implements SimpleCriterionTrigger.SimpleInstance {

	@Override
	public Optional<ContextAwarePredicate> player() {
		return Optional.empty();
	}

}

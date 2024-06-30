package dev.xkmc.l2core.serial.advancements;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;

import java.util.Optional;

@SerialClass
public class BaseCriterionInstance<T extends BaseCriterionInstance<T, R>, R extends BaseCriterion<T, R>>
		implements SimpleCriterionTrigger.SimpleInstance {

	private final R r;

	protected BaseCriterionInstance(R r) {
		this.r = r;
	}

	@Override
	public Optional<ContextAwarePredicate> player() {
		return Optional.empty();
	}

	public Criterion<T> build() {
		return new Criterion<>(r, Wrappers.cast(this));
	}

}

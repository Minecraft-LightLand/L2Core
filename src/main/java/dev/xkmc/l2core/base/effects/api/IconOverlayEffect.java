package dev.xkmc.l2core.base.effects.api;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IconOverlayEffect extends ClientRenderEffect {

	@Override
	default void render(Supplier<LivingEntityRenderState> entity, int lv, Consumer<DelayedEntityRender> adder) {
		adder.accept(getIcon(entity, lv));
	}

	DelayedEntityRender getIcon(Supplier<LivingEntityRenderState> entity, int lv);

}

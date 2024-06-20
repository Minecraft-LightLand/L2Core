package dev.xkmc.l2core.compat.curios;

import java.util.ArrayList;

import static dev.xkmc.l2core.compat.curios.CurioSlotBuilder.Operation.SET;

public record CurioSlotBuilder(int order, String icon, int size,
							   Operation operation,
							   boolean add_cosmetic,
							   boolean use_native_gui,
							   boolean render_toggle,
							   boolean replace, ArrayList<SlotCondition> conditions) {

	public CurioSlotBuilder(int order, String icon) {
		this(order, icon, 1, SET);
	}

	public CurioSlotBuilder(int order, String icon, int size,
							Operation operation) {
		this(order, icon, size, operation,
				false, true, true, false, SlotCondition.of());
	}

	public enum Operation {
		SET, ADD, REMOVE
	}

}

package dev.xkmc.l2core.compat.curios;

import java.util.ArrayList;

public record SlotCondition(String type, String modid) {

	public static ArrayList<SlotCondition> of(String... ids) {
		ArrayList<SlotCondition> ans = new ArrayList<>();
		for (String id : ids) {
			ans.add(new SlotCondition(id));
		}
		return ans;
	}

	public SlotCondition(String modid) {
		this("forge:mod_loaded", modid);
	}

}

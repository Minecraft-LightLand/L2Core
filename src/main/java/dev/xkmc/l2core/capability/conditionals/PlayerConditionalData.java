package dev.xkmc.l2core.capability.conditionals;

import dev.xkmc.l2core.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@SerialClass
public class PlayerConditionalData extends PlayerCapabilityTemplate<PlayerConditionalData> implements ConditionalData {

	@SerialField
	protected LinkedHashMap<TokenKey<?>, ConditionalToken> data = new LinkedHashMap<>();

	public LinkedHashMap<TokenKey<?>, ConditionalToken> data() {
		return data;
	}

	@Override
	public void onClone(Player player, boolean isWasDeath) {
		if (!isWasDeath) return;
		List<TokenKey<?>> toRemove = new ArrayList<>();
		for (var e : data.entrySet()) {
			if (!e.getValue().retainOnDeath(player)) {
				toRemove.add(e.getKey());
			}
		}
		for (var e : toRemove)
			data.remove(e);
	}

	@Override
	public void tick(Player player) {
		tickLogic(player);
	}

}

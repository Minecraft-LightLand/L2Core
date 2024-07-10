package dev.xkmc.l2core.capability.conditionals;

import dev.xkmc.l2core.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Supplier;

@SerialClass
public class ConditionalData extends PlayerCapabilityTemplate<ConditionalData> {

	@SerialField
	public LinkedHashMap<TokenKey<?>, ConditionalToken> data = new LinkedHashMap<>();

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

	public <T extends ConditionalToken> T getOrCreateData(TokenKey<T> setEffect, Supplier<T> fallback) {
		return Wrappers.cast(data.computeIfAbsent(setEffect, k -> fallback.get()));
	}

	@Nullable
	public <T extends ConditionalToken> T getData(TokenKey<T> setEffect) {
		return Wrappers.cast(data.get(setEffect));
	}

	@Override
	public void tick(Player player) {
		List<TokenKey<?>> toRemove = new ArrayList<>();
		for (var e : data.entrySet()) {
			if (e.getValue().tick(player)) {
				toRemove.add(e.getKey());
			}
		}
		for (var e : toRemove)
			data.remove(e);
	}

	public boolean hasData(TokenKey<?> eff) {
		return data.containsKey(eff);
	}

}

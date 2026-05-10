package dev.xkmc.l2core.capability.conditionals;

import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface ConditionalData {

	static ConditionalData of(LivingEntity le) {
		if (le instanceof Player player) {
			return L2LibReg.PLAYER_CONDITIONAL.type().getOrCreate(player);
		} else {
			return L2LibReg.MOB_CONDITIONAL.type().getOrCreate(le);
		}
	}

	static Optional<? extends ConditionalData> ofNullable(LivingEntity le) {
		if (le instanceof Player player) {
			return L2LibReg.PLAYER_CONDITIONAL.type().getExisting(player);
		} else {
			return L2LibReg.MOB_CONDITIONAL.type().getExisting(le);
		}
	}

	LinkedHashMap<TokenKey<?>, ConditionalToken> data();

	default boolean hasData(TokenKey<?> eff) {
		return data().containsKey(eff);
	}

	default <T extends ConditionalToken> T getOrCreateData(TokenKey<T> setEffect, Supplier<T> fallback) {
		return Wrappers.cast(data().computeIfAbsent(setEffect, k -> fallback.get()));
	}

	@Nullable
	default <T extends ConditionalToken> T getData(TokenKey<T> setEffect) {
		return Wrappers.cast(data().get(setEffect));
	}

	default @Nullable <T extends ConditionalToken> T put(TokenKey<T> key, T token) {
		return Wrappers.cast(data().put(key, token));
	}

	default void tickLogic(LivingEntity entity) {
		if (data().isEmpty()) return;
		List<TokenKey<?>> toRemove = new ArrayList<>();
		for (var e : data().entrySet()) {
			if (e.getValue().tick(entity)) {
				toRemove.add(e.getKey());
			}
		}
		for (var e : toRemove)
			data().remove(e);
	}

	default void onStartTracking(LivingEntity entity, ServerPlayer observer) {
		if (data().isEmpty()) return;
		for (var ent : data().entrySet()) {
			if (ent.getValue() instanceof NetworkSensitiveToken<?> nt && nt.broadcastOnTracking()) {
				nt.sync(Wrappers.cast(ent.getKey()), Wrappers.cast(ent.getValue()), entity, observer);
			}
		}
	}

}

package dev.xkmc.l2core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

import java.util.Set;

public class TeleportTool {

	public static void teleportHome(ServerLevel world, ServerPlayer player) {
		DimensionTransition respawn = player.findRespawnPositionAndUseSpawnBlock(true, DimensionTransition.DO_NOTHING);
		if (world == respawn.newLevel()) {
			player.moveTo(respawn.pos(), respawn.yRot(), respawn.xRot());
		} else {
			var pos = respawn.pos();
			performTeleport(player, respawn.newLevel(), pos.x, pos.y, pos.z, respawn.yRot(), respawn.xRot());
		}
	}

	public static void performTeleport(Entity e, ServerLevel level, double x, double y, double z, float yaw, float pitch) {
		EntityTeleportEvent.TeleportCommand event = EventHooks.onEntityTeleportCommand(e, x, y, z);
		if (event.isCanceled()) return;
		x = event.getTargetX();
		y = event.getTargetY();
		z = event.getTargetZ();
		BlockPos blockpos = BlockPos.containing(x, y, z);
		if (!Level.isInSpawnableBounds(blockpos)) return;
		float yr = Mth.wrapDegrees(yaw);
		float xr = Mth.wrapDegrees(pitch);
		if (e.teleportTo(level, x, y, z, Set.of(), yr, xr)) {
			if (e instanceof LivingEntity le) {
				if (!le.isFallFlying()) {
					e.setDeltaMovement(e.getDeltaMovement().multiply(1.0, 0.0, 1.0));
					e.setOnGround(true);
				}
			}
			if (e instanceof PathfinderMob mob) {
				mob.getNavigation().stop();
			}
		}
	}

}

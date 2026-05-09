package dev.xkmc.l2core.content.explosion;

import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import java.util.Optional;

public class ExplosionHandler {

	public static void explode(
			BaseExplosion explosion
	) {
		var sl = explosion.base.level();
		if (EventHooks.onExplosionStart(sl, explosion)) return;
		int blockCount = explosion.explode();
		var center = explosion.base.pos();
		var r = explosion.base.r();
		var particle = explosion.particle;
		for (ServerPlayer player : sl.players()) {
			if (player.distanceToSqr(center) < 4096.0) {
				Optional<Vec3> playerKnockback = Optional.ofNullable(explosion.getHitPlayers().get(player));
				player.connection.send(new ClientboundExplodePacket(center, r, blockCount, playerKnockback,
						particle.particle(), particle.sound(), particle.blockParticles()));
			}
		}
	}

}

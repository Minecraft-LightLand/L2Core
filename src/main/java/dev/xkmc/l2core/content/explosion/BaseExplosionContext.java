package dev.xkmc.l2core.content.explosion;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public record BaseExplosionContext(ServerLevel level, Vec3 pos, float r) {
}

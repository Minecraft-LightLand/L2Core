package dev.xkmc.l2core.base.explosion;

import net.minecraft.world.level.Level;

public record BaseExplosionContext(Level level, double x, double y, double z, float r) {
}

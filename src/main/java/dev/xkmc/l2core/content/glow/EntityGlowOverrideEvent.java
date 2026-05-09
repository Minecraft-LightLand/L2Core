package dev.xkmc.l2core.content.glow;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.EntityEvent;
import org.jspecify.annotations.Nullable;

public class EntityGlowOverrideEvent extends EntityEvent {

	private boolean enable;
	@Nullable
	private Integer color;

	public EntityGlowOverrideEvent(Entity entity) {
		super(entity);
	}

	public void setEnabled() {
		enable = true;
	}

	public void setEnabled(int color) {
		enable = true;
		this.color = color;
	}

	public boolean isEnabled() {
		return enable;
	}

	@Nullable
	public Integer getColor() {
		return color;
	}

}

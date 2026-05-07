package dev.xkmc.l2core.base.menu.base;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;

import javax.annotation.Nullable;
import java.util.HashMap;

@SuppressWarnings("unused")
public record MenuLayoutConfig(int height, HashMap<String, Rect> side, HashMap<String, Rect> comp) {

	public static Identifier getTexture(Identifier id) {
		return Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/gui/container/" + id.getPath() + ".png");
	}

	/**
	 * get the location of the component on the GUI
	 */
	public Rect getComp(String key) {
		return comp.getOrDefault(key, Rect.ZERO);
	}

	/**
	 * Height of this GUI
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * The X position of the player inventory
	 */
	public int getPlInvX() {
		return 8;
	}

	/**
	 * The Y position of the player inventory
	 */
	public int getPlInvY() {
		return height - 82;
	}

	/**
	 * get the rectangle representing the sprite element on the sprite
	 */
	public Rect getSide(String key) {
		return side.getOrDefault(key, Rect.ZERO);
	}

	/**
	 * configure the coordinate of the slot
	 */
	public <T extends Slot> void getSlot(String key, SlotFactory<T> fac, SlotAcceptor con) {
		Rect c = getComp(key);
		for (int j = 0; j < c.ry; j++)
			for (int i = 0; i < c.rx; i++) {
				var slot = fac.getSlot(c.x + i * c.w, c.y + j * c.h);
				if (slot != null) {
					con.addSlot(key, i, j, slot);
				}
			}
	}

	public int getWidth() {
		return 176;
	}

	/**
	 * return if the coordinate is within the rectangle represented by the key
	 */
	public boolean within(String key, double x, double y) {
		Rect c = getComp(key);
		return x > c.x && x < c.x + c.w && y > c.y && y < c.y + c.h;
	}

	public interface SlotFactory<T extends Slot> {

		@Nullable
		T getSlot(int x, int y);

	}

	public interface SlotAcceptor {

		void addSlot(String name, int i, int j, Slot slot);

	}

	@SerialClass
	public static class Rect {

		public static final Rect ZERO = new Rect();

		@SerialField
		public int x, y, w, h, rx = 1, ry = 1;

		public Rect() {
		}

	}

}

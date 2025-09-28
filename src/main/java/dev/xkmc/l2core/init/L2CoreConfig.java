package dev.xkmc.l2core.init;

import dev.xkmc.l2core.events.ClientEventHandler;
import dev.xkmc.l2core.util.ConfigInit;
import net.neoforged.neoforge.common.ModConfigSpec;

public class L2CoreConfig {

	public static class Client extends ConfigInit {

		public final ModConfigSpec.IntValue overlayZVal;
		public final ModConfigSpec.EnumValue<ClientEventHandler.EnchDesc> addEnchantmentDescription;
		public final ModConfigSpec.BooleanValue renderOverlayIcons;

		Client(Builder builder) {
			markL2();
			overlayZVal = builder.text("The height of item character overlay")
					.defineInRange("overlayZVal", 250, -1000000, 1000000);
			addEnchantmentDescription = builder.text("Show Enchantment Descriptions when there are enchantments with custom parametric descriptions")
					.defineEnum("addEnchantmentDescription", ClientEventHandler.EnchDesc.ALWAYS);
			renderOverlayIcons = builder.text("Render overlay icons on entities")
					.define("renderOverlayIcons", true);
		}

	}

	public static final Client CLIENT = L2Core.REGISTRATE.registerClient(Client::new);

	public static void init() {
	}

}

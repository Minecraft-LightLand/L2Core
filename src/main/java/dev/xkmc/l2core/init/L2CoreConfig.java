package dev.xkmc.l2core.init;

import dev.xkmc.l2core.events.ClientEventHandler;
import dev.xkmc.l2core.util.ConfigInit;
import net.neoforged.neoforge.common.ModConfigSpec;

public class L2CoreConfig {

	public static class Client extends ConfigInit {

		public final ModConfigSpec.IntValue overlayZVal;
		public final ModConfigSpec.EnumValue<ClientEventHandler.EnchDesc> addEnchantmentDescription;

		Client(Builder builder) {
			markL2();
			overlayZVal = builder.text("The height of item character overlay")
					.defineInRange("overlayZVal", 250, -1000000, 1000000);
			addEnchantmentDescription = builder.text("Add Enchantment Descriptions")
					.defineEnum("addEnchantmentDescription", ClientEventHandler.EnchDesc.ALWAYS);
		}

	}

	public static final Client CLIENT = L2Core.REGISTRATE.registerClient(Client::new);

	public static void init() {
	}

}

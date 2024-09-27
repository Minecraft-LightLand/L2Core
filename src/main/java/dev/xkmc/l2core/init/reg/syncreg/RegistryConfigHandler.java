package dev.xkmc.l2core.init.reg.syncreg;

import com.google.gson.JsonElement;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public interface RegistryConfigHandler {

	JsonElement serializeConfig();

	@Nullable
	Component verifyConfig(JsonElement value);

	void applyConfig(JsonElement value);

}

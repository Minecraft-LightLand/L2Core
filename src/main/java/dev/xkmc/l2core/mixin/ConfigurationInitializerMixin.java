package dev.xkmc.l2core.mixin;

import dev.xkmc.l2core.init.reg.syncreg.SyncRegistryConfigTask;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.network.ConfigurationInitialization;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ConfigurationInitialization.class)
public class ConfigurationInitializerMixin {

	@Inject(at = @At("HEAD"), method = "configureEarlyTasks")
	private static void l2core$configureEarlyTasks(ServerConfigurationPacketListener listener, Consumer<ConfigurationTask> tasks, CallbackInfo ci) {
		SyncRegistryConfigTask.addTask(listener, tasks);
	}

}

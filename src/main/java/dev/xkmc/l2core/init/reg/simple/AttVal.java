package dev.xkmc.l2core.init.reg.simple;

import dev.xkmc.l2core.capability.attachment.AttachmentDef;
import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2core.capability.attachment.GeneralCapabilityTemplate;
import dev.xkmc.l2core.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2core.capability.player.PlayerCapabilityTemplate;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.attachment.AttachmentType;

public interface AttVal<T, H extends AttachmentDef<T>> extends Val<AttachmentType<T>> {

	H type();

	interface CapVal<E extends AttachmentHolder, T extends GeneralCapabilityTemplate<E, T>>
			extends AttVal<T, GeneralCapabilityHolder<E, T>> {

	}

	interface PlayerVal<T extends PlayerCapabilityTemplate<T>>
			extends AttVal<T, PlayerCapabilityHolder<T>> {

	}

}

package dev.xkmc.l2core.capability.conditionals;

import dev.xkmc.l2core.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.TreeSet;

@SerialClass
public class PlayerFlagData extends PlayerCapabilityTemplate<PlayerFlagData> {

    public static void addFlag(LivingEntity entity, String str) {
        if (entity instanceof Player player) {
            L2LibReg.FLAGS.type().getOrCreate(player).addFlag(str);
            if (player instanceof ServerPlayer sp)
                L2LibReg.FLAGS.type().network.toClient(sp);
        } else {
            entity.addTag(str);
        }
    }

    public static boolean hasFlag(LivingEntity entity, String str) {
        if (entity instanceof Player player) {
            return L2LibReg.FLAGS.type().getExisting(player).map(e -> e.hasFlag(str)).orElse(false);
        } else {
            return entity.getTags().contains(str);
        }
    }

    @SerialField
    private final TreeSet<String> flags = new TreeSet<>();

    public void addFlag(String str) {
        flags.add(str);
    }

    public boolean hasFlag(String flag) {
        return flags.contains(flag);
    }

    public static void register() {

    }

}

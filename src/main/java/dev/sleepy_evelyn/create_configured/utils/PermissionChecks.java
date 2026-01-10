package dev.sleepy_evelyn.create_configured.utils;

import dev.sleepy_evelyn.create_configured.permissions.CCPermissionNodes;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.permission.PermissionAPI;

public class PermissionChecks {

    public static boolean canBypassTrainDisassembly(ServerPlayer player) {
        return player.hasPermissions(4) || PermissionAPI.getPermission(player,
                CCPermissionNodes.BYPASS_TRAIN_DISASSEMBLY);
    }
}

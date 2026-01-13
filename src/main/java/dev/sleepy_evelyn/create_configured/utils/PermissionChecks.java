package dev.sleepy_evelyn.create_configured.utils;

import dev.sleepy_evelyn.create_configured.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.permissions.CCPermissionNodes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.server.permission.PermissionAPI;

import java.util.UUID;

import static dev.sleepy_evelyn.create_configured.CreateConfiguredServer.groupsProvider;

public class PermissionChecks {

    @OnlyIn(Dist.DEDICATED_SERVER)
    public static boolean canPlayerDisassembleTrain(ServerPlayer player, UUID trainOwner, TrainDisassemblyLock lock) {
        if(canBypassTrainDisassembly(player)) return true;
        return canDisassembleTrain(player.server, player.getUUID(), trainOwner, lock);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public static boolean canDisassembleTrain(MinecraftServer server, UUID disassembler, UUID trainOwner, TrainDisassemblyLock lock) {
        if (lock == TrainDisassemblyLock.PARTY_MEMBERS_ONLY && groupsProvider().isPresent())
            return groupsProvider().get().getMemberRank(server, trainOwner, disassembler).isPresent();
        return lock != TrainDisassemblyLock.LOCKED || (disassembler.equals(trainOwner));
    }

    public static boolean canBypassTrainDisassembly(ServerPlayer player) {
        return player.hasPermissions(4) || PermissionAPI.getPermission(player,
                CCPermissionNodes.BYPASS_TRAIN_DISASSEMBLY);
    }
}

package dev.sleepy_evelyn.create_configured.utils;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import dev.sleepy_evelyn.create_configured.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import dev.sleepy_evelyn.create_configured.groups.MemberRank;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.server.DisassemblyLockable;
import dev.sleepy_evelyn.create_configured.permissions.CCPermissionNodes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

import static dev.sleepy_evelyn.create_configured.CreateConfiguredServer.groupsProvider;

public class TrainHelper {

    public static Optional<Train> getOwnedTrain(StationBlockEntity sbe) {
        return Optional.ofNullable(sbe)
                .map(StationBlockEntity::getStation)
                .map(GlobalStation::getPresentTrain)
                .filter(train -> train.owner != null);
    }

    public static boolean canBypassDisassembly(ServerPlayer player) {
        return player.hasPermissions(4) || PermissionAPI.getPermission(player,
                CCPermissionNodes.BYPASS_TRAIN_DISASSEMBLY);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public static boolean canPlayerDisassemble(ServerPlayer player, @Nullable Train train) {
        if(canBypassDisassembly(player)) return true;
        return canDisassemble(player.server, player.getUUID(), train);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public static boolean canDisassemble(MinecraftServer server, @Nullable UUID disassembler, @Nullable Train train) {
        if (train == null || disassembler == null) return false;
        if (!CCConfigs.server().lockTrainDisassembly.get()) return true;

        var lock = ((DisassemblyLockable) train).cc$getLock();
        var owner = train.owner;

        if (owner == null) return true;
        else if (lock == TrainDisassemblyLock.PARTY_MEMBERS && groupsProvider().isPresent()) {
            var memberRank = groupsProvider().get().getMemberRank(server, owner, disassembler);
            return memberRank.map(rank -> rank != MemberRank.NONE)
                    .orElseGet(() -> (disassembler.equals(owner)));
        }
        return lock != TrainDisassemblyLock.LOCKED || (disassembler.equals(owner));
    }
}

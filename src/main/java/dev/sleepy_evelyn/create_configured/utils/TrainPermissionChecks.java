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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

import static dev.sleepy_evelyn.create_configured.CreateConfiguredServer.groupsProvider;

public class TrainPermissionChecks {

    public static Optional<Train> getOwnedTrain(StationBlockEntity sbe) {
        return Optional.ofNullable(sbe)
                .map(StationBlockEntity::getStation)
                .map(GlobalStation::getPresentTrain)
                .filter(train -> train.owner != null);
    }

    public static boolean canBypassDisassembly(@NotNull ServerPlayer player) {
        return player.hasPermissions(4) || PermissionAPI.getPermission(player,
                CCPermissionNodes.BYPASS_TRAIN_DISASSEMBLY);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public static boolean canDisassemble(@Nullable ServerPlayer disassembler, @NotNull Train train) {
        if (disassembler == null) return isUnlocked(train) || isLockingDisabled();
        if (canBypassDisassembly(disassembler)) return true;
        return canDisassemble(disassembler.server, disassembler.getUUID(), train);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public static boolean canDisassemble(MinecraftServer server, @Nullable UUID disassembler, @NotNull Train train) {
        if (disassembler == null) return isUnlocked(train) || isLockingDisabled();
        if (isLockingDisabled()) return true;

        var lock = ((DisassemblyLockable) train).cc$getLock();
        UUID owner = train.owner;

        // No Train owner so it shouldn't restrict
        if (owner == null) return true;

        // Party member lock considered only if a group provider is present
        if (lock == TrainDisassemblyLock.PARTY_MEMBERS && groupsProvider().isPresent()) {
            return groupsProvider().get()
                    .getMemberRank(server, owner, disassembler)
                    .map(rank -> rank != MemberRank.NONE)
                    .orElse(disassembler.equals(owner));
        }
        // Only returns false if it's locked & the disassembler isn't the trains owner
        return lock != TrainDisassemblyLock.LOCKED || (disassembler.equals(owner));
    }

    private static boolean isUnlocked(@NotNull Train train) {
        return ((DisassemblyLockable) train).cc$getLock() == TrainDisassemblyLock.NOT_LOCKED;
    }

    private static boolean isLockingDisabled() {
        return !CCConfigs.server().lockTrainDisassembly.get();
    }
}

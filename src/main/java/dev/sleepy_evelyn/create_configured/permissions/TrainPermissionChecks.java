package dev.sleepy_evelyn.create_configured.permissions;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import dev.sleepy_evelyn.create_configured.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import dev.sleepy_evelyn.create_configured.groups.MemberRank;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.server.TrainTweaks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.isDedicatedServer;
import static dev.sleepy_evelyn.create_configured.CreateConfiguredServer.groupsProvider;

public class TrainPermissionChecks {

    public static boolean canBypassDisassembly(@NotNull ServerPlayer player) {
        return player.hasPermissions(4) || PermissionAPI.getPermission(player,
                CCPermissionNodes.BYPASS_TRAIN_DISASSEMBLY);
    }

    public static boolean canDisassemble(@Nullable ServerPlayer disassembler, @NotNull Train train) {
        if (!isDedicatedServer()) return true;
        if (disassembler == null) return isUnlocked(train) || isLockingDisabled();
        if (canBypassDisassembly(disassembler)) return true;
        return canDisassemble(disassembler.server, disassembler.getUUID(), train);
    }

    public static boolean canDisassemble(MinecraftServer server, @Nullable UUID disassembler, @NotNull Train train) {
        if (!isDedicatedServer()) return true;
        if (disassembler == null) return isUnlocked(train) || isLockingDisabled();
        if (isLockingDisabled()) return true;

        var lock = ((TrainTweaks) train).cc$getLock();
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
        return ((TrainTweaks) train).cc$getLock() == TrainDisassemblyLock.NOT_LOCKED;
    }

    private static boolean isLockingDisabled() {
        return !CCConfigs.server().lockTrainDisassembly.get();
    }
}

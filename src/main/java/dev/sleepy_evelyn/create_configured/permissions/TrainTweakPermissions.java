package dev.sleepy_evelyn.create_configured.permissions;

import com.simibubi.create.content.trains.entity.Train;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import org.jetbrains.annotations.NotNull;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.isDedicatedServer;

public record TrainTweakPermissions(boolean canDisassemble, boolean canChangeTopSpeed, boolean canChangeAcceleration) {

    public TrainTweakPermissions() {
        this(true, true, true);
    }

    public static final StreamCodec<FriendlyByteBuf, TrainTweakPermissions> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, TrainTweakPermissions::canDisassemble,
                    ByteBufCodecs.BOOL, TrainTweakPermissions::canChangeTopSpeed,
                    ByteBufCodecs.BOOL, TrainTweakPermissions::canChangeAcceleration,
                    TrainTweakPermissions::new
            );

    public static TrainTweakPermissions resolve(@NotNull ServerPlayer player, @NotNull Train train) {
        var config = CCConfigs.server().trainTweaksConfig;
        boolean canBypassTrainTweaks = !isDedicatedServer() ||
                PermissionAPI.getPermission(player, CCPermissionNodes.BYPASS_TRAIN_TWEAKS);
        boolean canChangeTopSpeed = canBypassTrainTweaks || config.canPlayerChangeMaxSpeed.get();
        boolean canChangeAcceleration = canBypassTrainTweaks || config.canPlayerChangeAcceleration.get();

        return new TrainTweakPermissions(TrainPermissionChecks.canDisassemble(player, train),
                canChangeTopSpeed, canChangeAcceleration);
    }
}

package dev.sleepy_evelyn.create_configured.permissions;

import com.simibubi.create.content.trains.entity.Train;
import dev.sleepy_evelyn.create_configured.trains.TrainMotionProfile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

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
        return new TrainTweakPermissions(
                TrainPermissionChecks.canDisassemble(player, train),
                TrainPermissionChecks.canChangeMotionProfile(player, TrainMotionProfile.Type.TOP_SPEED),
                TrainPermissionChecks.canChangeMotionProfile(player, TrainMotionProfile.Type.ACCELERATION)
        );
    }
}

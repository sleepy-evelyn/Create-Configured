package dev.sleepy_evelyn.create_configured.network.s2c;

import dev.sleepy_evelyn.create_configured.trains.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.trains.TrainMotionProfile;
import dev.sleepy_evelyn.create_configured.permissions.TrainTweakPermissions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.rl;

public record StationScreenSyncPayload(TrainTweakPermissions permissions, TrainDisassemblyLock lock, TrainMotionProfile topSpeed, TrainMotionProfile acceleration) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StationScreenSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(rl("station_screen_sync"));

    public static final StreamCodec<FriendlyByteBuf, StationScreenSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    TrainTweakPermissions.STREAM_CODEC, StationScreenSyncPayload::permissions,
                    TrainDisassemblyLock.ID_STREAM_CODEC, StationScreenSyncPayload::lock,
                    TrainMotionProfile.ID_STREAM_CODEC, StationScreenSyncPayload::topSpeed,
                    TrainMotionProfile.ID_STREAM_CODEC, StationScreenSyncPayload::acceleration,
                    StationScreenSyncPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package dev.sleepy_evelyn.create_configured.network.c2s;

import dev.sleepy_evelyn.create_configured.TrainMotionProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.rl;

public record ChangeMotionProfilePayload(BlockPos stationPos, TrainMotionProfile topSpeed, TrainMotionProfile acceleration) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ChangeMotionProfilePayload> TYPE =
            new CustomPacketPayload.Type<>(rl("change_train_motion_profile"));

    public static final StreamCodec<FriendlyByteBuf, ChangeMotionProfilePayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, ChangeMotionProfilePayload::stationPos,
                    TrainMotionProfile.ID_STREAM_CODEC, ChangeMotionProfilePayload::topSpeed,
                    TrainMotionProfile.ID_STREAM_CODEC, ChangeMotionProfilePayload::acceleration,
                    ChangeMotionProfilePayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

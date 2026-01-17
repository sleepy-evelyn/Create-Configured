package dev.sleepy_evelyn.create_configured.network.c2s;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.rl;

public record NotifyTrainAtStation(BlockPos stationPos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<NotifyTrainAtStation> TYPE =
            new CustomPacketPayload.Type<>(rl("notify_train_at_station"));

    public static final StreamCodec<FriendlyByteBuf, NotifyTrainAtStation> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, NotifyTrainAtStation::stationPos,
                    NotifyTrainAtStation::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

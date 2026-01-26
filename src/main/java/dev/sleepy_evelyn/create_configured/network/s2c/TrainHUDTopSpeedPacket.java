package dev.sleepy_evelyn.create_configured.network.s2c;

import dev.sleepy_evelyn.create_configured.TrainMotionProfile;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.rl;

public record TrainHUDTopSpeedPacket(UUID trainId, TrainMotionProfile topSpeed) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TrainHUDTopSpeedPacket> TYPE =
            new CustomPacketPayload.Type<>(rl("train_hud_update_top_speed"));

    public static final StreamCodec<FriendlyByteBuf, TrainHUDTopSpeedPacket> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, TrainHUDTopSpeedPacket::trainId,
                    TrainMotionProfile.ID_STREAM_CODEC, TrainHUDTopSpeedPacket::topSpeed,
                    TrainHUDTopSpeedPacket::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package dev.sleepy_evelyn.create_configured.network.s2c;

import dev.sleepy_evelyn.create_configured.TrainDisassemblyLock;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.rl;

public record StationScreenSyncPayload(boolean canPlayerDisassemble, TrainDisassemblyLock lock) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StationScreenSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(rl("station_screen_sync"));

    public static final StreamCodec<FriendlyByteBuf, StationScreenSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, StationScreenSyncPayload::canPlayerDisassemble,
                    TrainDisassemblyLock.ID_STREAM_CODEC, StationScreenSyncPayload::lock,
                    StationScreenSyncPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

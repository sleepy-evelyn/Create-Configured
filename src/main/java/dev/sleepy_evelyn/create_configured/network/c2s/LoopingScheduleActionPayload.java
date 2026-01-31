package dev.sleepy_evelyn.create_configured.network.c2s;

import com.simibubi.create.content.trains.schedule.Schedule;
import dev.sleepy_evelyn.create_configured.trains.LoopingScheduleAction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.rl;

public record LoopingScheduleActionPayload(Schedule schedule, LoopingScheduleAction action) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<LoopingScheduleActionPayload> TYPE =
            new CustomPacketPayload.Type<>(rl("looping_schedule_action"));

    public static final StreamCodec<RegistryFriendlyByteBuf, LoopingScheduleActionPayload> STREAM_CODEC =
            StreamCodec.composite(
                    Schedule.STREAM_CODEC, LoopingScheduleActionPayload::schedule,
                    LoopingScheduleAction.ID_STREAM_CODEC, LoopingScheduleActionPayload::action,
                    LoopingScheduleActionPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

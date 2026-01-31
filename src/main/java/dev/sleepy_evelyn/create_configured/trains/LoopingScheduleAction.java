package dev.sleepy_evelyn.create_configured.trains;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum LoopingScheduleAction {
    APPROVE(0),
    DENY(1),
    NO_ACTION(2),
    SEND(3);

    public static final IntFunction<LoopingScheduleAction> BY_ID =
            ByIdMap.continuous(
                    LoopingScheduleAction::getId,
                    LoopingScheduleAction.values(),
                    ByIdMap.OutOfBoundsStrategy.ZERO
            );

    public static final StreamCodec<ByteBuf, LoopingScheduleAction> ID_STREAM_CODEC =
            ByteBufCodecs.idMapper(LoopingScheduleAction.BY_ID, LoopingScheduleAction::getId);

    private final int id;

    LoopingScheduleAction(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

package dev.sleepy_evelyn.create_configured;

import dev.sleepy_evelyn.create_configured.gui.CCGuiTextures;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum TrainDisassemblyLock {
    LOCKED(0, CCGuiTextures.TRAIN_DISASSEMBLY_LOCK_CLOSED),
    NOT_LOCKED(1, CCGuiTextures.TRAIN_DISASSEMBLY_LOCK_OPEN),
    PARTY_MEMBERS(2, CCGuiTextures.TRAIN_DISASSEMBLY_LOCK_WARN);

    public static final IntFunction<TrainDisassemblyLock> BY_ID =
            ByIdMap.continuous(
                    TrainDisassemblyLock::getId,
                    TrainDisassemblyLock.values(),
                    ByIdMap.OutOfBoundsStrategy.ZERO
            );

    public static final StreamCodec<ByteBuf, TrainDisassemblyLock> ID_STREAM_CODEC =
            ByteBufCodecs.idMapper(TrainDisassemblyLock.BY_ID, TrainDisassemblyLock::getId);

    private final int id;
    private final CCGuiTextures texture;

    TrainDisassemblyLock(int id, CCGuiTextures texture) {
        this.id = id;
        this.texture = texture;
    }

    public int getId() { return id; }

    public CCGuiTextures getTexture() { return texture; }

    public Component getTooltipComponent(String suffix, ChatFormatting... style) {
        return Component.translatable(String.format("create_configured.gui.station.disassembly_lock.%s.%s",
                        name().toLowerCase(), suffix)).withStyle(style);
    }
}

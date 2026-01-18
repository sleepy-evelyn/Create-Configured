package dev.sleepy_evelyn.create_configured;

import dev.sleepy_evelyn.create_configured.gui.CCGuiTextures;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum TrainSpeed {
    FAST(0, CCGuiTextures.TRAIN_SPEED_MODIFIER_FAST),
    DEFAULT(1, CCGuiTextures.TRAIN_SPEED_MODIFIER_DEFAULT),
    SLOW(2, CCGuiTextures.TRAIN_SPEED_MODIFIER_SLOW);

    public static final IntFunction<TrainSpeed> BY_ID =
            ByIdMap.continuous(
                TrainSpeed::getId,
                TrainSpeed.values(),
                ByIdMap.OutOfBoundsStrategy.ZERO
            );

    public static final StreamCodec<ByteBuf, TrainSpeed> ID_STREAM_CODEC =
            ByteBufCodecs.idMapper(TrainSpeed.BY_ID, TrainSpeed::getId);

    private final int id;
    private final CCGuiTextures texture;

    public CCGuiTextures getTexture() { return texture; }

    TrainSpeed(int id, CCGuiTextures texture) {
        this.id = id;
        this.texture = texture;
    }

    public int getId() { return id; }
}

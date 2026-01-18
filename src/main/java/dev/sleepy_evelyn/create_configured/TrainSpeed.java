package dev.sleepy_evelyn.create_configured;

import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CServer;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import dev.sleepy_evelyn.create_configured.config.nested.TrainSpeedsConfig;
import dev.sleepy_evelyn.create_configured.gui.CCGuiTextures;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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

    TrainSpeed(int id, CCGuiTextures texture) {
        this.id = id;
        this.texture = texture;
    }

    public int getId() { return id; }

    public CCGuiTextures getTexture() { return texture; }

    public int getBlocksPerSecondSpeed() {
        float defaultTopSpeed = AllConfigs.server().trains.poweredTrainTopSpeed.getF();
        float multiplier = 1;
        TrainSpeedsConfig trainSpeedOptions = CCConfigs.server().trainSpeedOptions;

        if (this == FAST)
            multiplier = trainSpeedOptions.fastTopSpeedMultiplier.getF();
        else if (this == SLOW)
            multiplier = trainSpeedOptions.slowTopSpeedMultiplier.getF();

        return Math.round(defaultTopSpeed * multiplier);
    }

    public Component getTooltipNameComponent() {
        return Component.translatable(String.format("create_configured.gui.station.train_speed.%s", name().toLowerCase()));
    }

    public Component getBpsTooltipComponent(int line) {
        final String keyPrefix = "create_configured.gui.station.train_speed.blocks_per_second_";

        return (line == 1
                ? Component.translatable(keyPrefix + line)
                : Component.translatable(keyPrefix + line, getBlocksPerSecondSpeed())
        ).withStyle(ChatFormatting.GRAY);
    }
}

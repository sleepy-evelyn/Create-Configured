package dev.sleepy_evelyn.create_configured;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CTrains;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import dev.sleepy_evelyn.create_configured.config.nested.TrainTweaksConfig;
import dev.sleepy_evelyn.create_configured.gui.CCGuiTextures;
import dev.sleepy_evelyn.create_configured.gui.TriStateButton;
import dev.sleepy_evelyn.create_configured.utils.ScreenUtils;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.function.IntFunction;

public enum TrainMotionProfile implements TriStateButton {

    SLOW_TOP_SPEED(0, Type.TOP_SPEED, Rate.SLOW), // Slow top speed is depreciated. Kept to maintain compatibility
    DEFAULT_TOP_SPEED(1, Type.TOP_SPEED, Rate.DEFAULT),
    FAST_TOP_SPEED(2, Type.TOP_SPEED, Rate.FAST),

    SLOW_ACCELERATION(3, Type.ACCELERATION, Rate.SLOW),
    DEFAULT_ACCELERATION(4, Type.ACCELERATION, Rate.DEFAULT),
    FAST_ACCELERATION(5, Type.ACCELERATION, Rate.FAST);

    public static final IntFunction<TrainMotionProfile> BY_ID =
            ByIdMap.continuous(
                TrainMotionProfile::getId,
                TrainMotionProfile.values(),
                ByIdMap.OutOfBoundsStrategy.ZERO
            );

    public static final StreamCodec<ByteBuf, TrainMotionProfile> ID_STREAM_CODEC =
            ByteBufCodecs.idMapper(TrainMotionProfile.BY_ID, TrainMotionProfile::getId);

    public enum Type {
        TOP_SPEED, ACCELERATION;
    }

    public enum Rate {
        SLOW, DEFAULT, FAST
    }

    private final int id;
    private final Type type;
    private final Rate rate;

    private final String typeKey, rateKey;
    private final CCGuiTextures texture;
    private final List<Component> cachedTooltip;

    TrainMotionProfile(int id, Type type, Rate rate) {
        this.id = id;
        this.type = type;
        this.rate = rate;
        this.typeKey = type.name().toLowerCase(Locale.ENGLISH);
        this.rateKey = rate.name().toLowerCase(Locale.ENGLISH);
        this.cachedTooltip = new LinkedList<>();

        switch (rate) {
            case FAST -> texture = CCGuiTextures.TRAIN_SPEED_MODIFIER_FAST;
            case SLOW -> texture = CCGuiTextures.TRAIN_SPEED_MODIFIER_SLOW;
            default -> texture = CCGuiTextures.TRAIN_SPEED_MODIFIER_DEFAULT;
        }
    }

    public Rate getRate() { return rate; }
    public Type getType() { return type; }

    @Override
    public int getId() { return id; }

    @Override
    public TrainMotionProfile nextState() {
        if (type == Type.TOP_SPEED) return switch(rate) {
            case FAST, SLOW -> TrainMotionProfile.DEFAULT_TOP_SPEED;
            case DEFAULT -> TrainMotionProfile.FAST_TOP_SPEED;
        };
        else return switch (rate) {
            case FAST -> TrainMotionProfile.SLOW_ACCELERATION;
            case DEFAULT -> TrainMotionProfile.FAST_ACCELERATION;
            case SLOW -> TrainMotionProfile.DEFAULT_ACCELERATION;
        };
    }

    @Override
    public CCGuiTextures getTexture() { return texture; }

    @Override
    public List<Component> getTooltip() {
        if (!cachedTooltip.isEmpty()) return cachedTooltip;

        String prefix = "create_configured.gui.station.train_";
        cachedTooltip.add(Component.translatable(prefix + typeKey + "." + rateKey));

        String bpsPrefix = prefix + typeKey + ".blocks_per_second" + (type == Type.ACCELERATION ? "_squared_" : "_");
        cachedTooltip.addAll(List.of(
                Component.translatable(bpsPrefix + 1, getMotionValue( true, true))
                        .withStyle(ChatFormatting.GRAY),
                Component.translatable(bpsPrefix + 2, getMotionValue(false, true))
                        .withStyle(ChatFormatting.GRAY)
        ));
        if (rate != Rate.DEFAULT)
            cachedTooltip.add(Component.translatable(prefix + "fuel_consumption." + rateKey)
                    .withStyle(rate == Rate.SLOW ? ChatFormatting.GREEN : ChatFormatting.RED));

        cachedTooltip.add(ScreenUtils.Tooltip.switchStateComponent());
        return cachedTooltip;
    }

    public float getMultiplier(boolean poweredTrain) {
        if (rate == Rate.DEFAULT || (rate == Rate.FAST && !poweredTrain)) return 1;
        TrainTweaksConfig tsc = CCConfigs.server().trainTweaksConfig;
        float multiplier;

        if (rate == Rate.FAST)
            return type == Type.TOP_SPEED ? tsc.fastTopSpeedMultiplier.getF() : tsc.fastAccelerationMultiplier.getF();
        else
            return type == Type.TOP_SPEED ? 1 : tsc.slowAccelerationMultiplier.getF();
    }

    public float getMotionValue(boolean poweredTrain, boolean rounded) {
        CTrains ct = AllConfigs.server().trains;

        float defaultValue = (switch (type) {
            case TOP_SPEED -> poweredTrain ? ct.poweredTrainTopSpeed : ct.trainTopSpeed;
            case ACCELERATION -> poweredTrain ? ct.poweredTrainAcceleration : ct.trainAcceleration;
        }).getF();

        float finalValue = defaultValue * getMultiplier(poweredTrain);
        return rounded ? Math.round(finalValue * 10) / 10f : finalValue;
    }
}

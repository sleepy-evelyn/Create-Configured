package dev.sleepy_evelyn.create_configured.trains;

import dev.sleepy_evelyn.create_configured.CreateConfiguredClient;
import dev.sleepy_evelyn.create_configured.gui.CCGuiTextures;
import dev.sleepy_evelyn.create_configured.gui.TriStateButton;
import dev.sleepy_evelyn.create_configured.utils.ScreenUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.List;
import java.util.Locale;
import java.util.function.IntFunction;

public enum TrainDisassemblyLock implements TriStateButton {

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
    private final List<Component> tooltip;

    TrainDisassemblyLock(int id, CCGuiTextures texture) {
        this.id = id;
        this.texture = texture;

        String prefix = "create_configured.gui.station.disassembly_lock.";
        String nameKey = name().toLowerCase(Locale.ENGLISH);

        tooltip = List.of(
                Component.translatable(prefix + nameKey + "." + "title")
                        .withStyle(ChatFormatting.WHITE),
                Component.translatable(prefix + nameKey + "." + "description")
                        .withStyle(ChatFormatting.GRAY),
                ScreenUtils.Tooltip.switchStateComponent()
        );
    }

    @Override
    public int getId() { return id; }

    @Override
    public TrainDisassemblyLock nextState() {
        boolean hasGroupProvider = !CreateConfiguredClient.groupsProviderId.contains("none");

        return switch(this) {
            case NOT_LOCKED -> hasGroupProvider ? TrainDisassemblyLock.PARTY_MEMBERS : TrainDisassemblyLock.LOCKED;
            case PARTY_MEMBERS -> TrainDisassemblyLock.LOCKED;
            case LOCKED -> TrainDisassemblyLock.NOT_LOCKED;
        };
    }

    @Override
    public CCGuiTextures getTexture() { return texture; }

    @Override
    public List<Component> getTooltip() { return tooltip; }
}

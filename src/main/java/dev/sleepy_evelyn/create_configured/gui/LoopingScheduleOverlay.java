package dev.sleepy_evelyn.create_configured.gui;

import net.createmod.catnip.theme.Color;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.translatable;

public class LoopingScheduleOverlay implements LayeredDraw.Layer {

    public static final LoopingScheduleOverlay OVERLAY = new LoopingScheduleOverlay();

    private final Set<UUID> selectedTrainIds = new HashSet<>();
    private @Nullable State currentState, prevState;
    private int ticks;

    public LoopingScheduleOverlay() {}

    @OnlyIn(Dist.CLIENT)
    public void onGuiTick() {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        /*
        if (currentState != prevState) {
            warmupTicks = 0;
            prevState = currentState;
        } else if (warmupTicks < 20)
            warmupTicks++;

        if (currentState == null) {

        }*/
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        if (currentState == null) return;

        var mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        MutableComponent text = currentState.getText();
        var window = mc.getWindow();
        int x = (window.getGuiScaledWidth() - mc.font.width(text)) / 2;
        int y = window.getGuiScaledHeight() - 61;
        var colour = new Color(0x4ADB4A).setAlpha(Mth.clamp((ticks - 4) / 3f, 0.1f, 1));

        guiGraphics.drawString(mc.font, text, x, y, colour.getRGB(), false);
    }


    enum State {
        SELECTING_TRAINS,
        TRAIN_SELECTED,
        SCHEDULE_SENT;

        private final MutableComponent text;

        State() {
            this.text = translatable("gui.overlay.looping_schedule." +
                    name().toLowerCase(Locale.ENGLISH));
        }

        public MutableComponent getText() { return text; }
    }
}

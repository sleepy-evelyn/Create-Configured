package dev.sleepy_evelyn.create_configured.utils;

import com.simibubi.create.content.trains.station.StationScreen;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.client.TrainTweaksSynced;
import dev.sleepy_evelyn.create_configured.network.s2c.StationScreenSyncPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public class ScreenUtils {
    public static Screen getCurrentScreen() {
        return Minecraft.getInstance().screen; // null when no GUI is open
    }

    public static <T extends Screen> Optional<T> getIfInstance(Class<T> clazz) {
        var screen = getCurrentScreen();
        return clazz.isInstance(screen) ? Optional.of(clazz.cast(screen)) : Optional.empty();
    }

    public static void syncStationScreen(StationScreenSyncPayload payload) {
        ScreenUtils.getIfInstance(StationScreen.class).ifPresent(stationScreen ->
                ((TrainTweaksSynced) stationScreen).cc$onSync(payload.permissions(), payload.lock(),
                        payload.topSpeed(), payload.acceleration()));
    }

    public static class Tooltip {

        public static Component switchStateComponent() {
            return Component.translatable("create_configured.gui.tooltip.switch_state")
                    .withStyle(ChatFormatting.DARK_GRAY)
                    .withStyle(ChatFormatting.ITALIC);
        }
    }
}


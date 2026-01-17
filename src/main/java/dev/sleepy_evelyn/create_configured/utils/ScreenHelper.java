package dev.sleepy_evelyn.create_configured.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.Optional;

public class ScreenHelper {
    public static Screen getCurrentScreen() {
        return Minecraft.getInstance().screen; // null when no GUI is open
    }

    public static <T extends Screen> Optional<T> getIfInstance(Class<T> clazz) {
        var screen = getCurrentScreen();
        return clazz.isInstance(screen) ? Optional.of(clazz.cast(screen)) : Optional.empty();
    }
}


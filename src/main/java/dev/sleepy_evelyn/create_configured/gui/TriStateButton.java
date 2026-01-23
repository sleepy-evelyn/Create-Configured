package dev.sleepy_evelyn.create_configured.gui;

import net.minecraft.network.chat.Component;

import java.util.List;

public interface TriStateButton {
    int getId();
    Object nextState();
    CCGuiTextures getTexture();
    List<Component> getTooltip();
}

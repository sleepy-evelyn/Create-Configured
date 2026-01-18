package dev.sleepy_evelyn.create_configured.gui;

import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.rl;

/*
 * MIT License
 *
 * Copyright (c) 2019 simibubi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 *         to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

public enum CCGuiTextures implements ScreenElement, TextureSheetSegment {

    TRAIN_DISASSEMBLY_LOCK_CLOSED("train_disassembly_lock", 0, 0, 16, 16, 32),
    TRAIN_DISASSEMBLY_LOCK_WARN("train_disassembly_lock", 16, 0, 16, 16, 32),
    TRAIN_DISASSEMBLY_LOCK_OPEN("train_disassembly_lock", 0, 16, 16, 16, 32),

    TRAIN_SPEED_MODIFIER_SLOW("train_speed_modifier", 0, 0, 16, 16, 32),
    TRAIN_SPEED_MODIFIER_DEFAULT("train_speed_modifier", 16, 0, 16, 16, 32),
    TRAIN_SPEED_MODIFIER_FAST("train_speed_modifier", 0, 16, 16, 16, 32);

    public final ResourceLocation location;
    private final int width, height, startX, startY, textureWidth, textureHeight;

    CCGuiTextures(String location, int startX, int startY, int width, int height) {
        this(location, startX, startY, width, height, 256);
    }

    CCGuiTextures(String location, int startX, int startY, int width, int height, int textureSize) {
        this.location = rl("textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
        this.textureWidth = textureSize;
        this.textureHeight = textureSize;
    }

    @Override
    public @NotNull ResourceLocation getLocation() {
        return location;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(location, x, y, startX, startY, width, height, textureWidth, textureHeight);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(graphics, c, x, y, 0, startX, startY, width, height,
                textureWidth, textureHeight);
    }

    @Override
    public int getStartX() {
        return startX;
    }

    @Override
    public int getStartY() {
        return startY;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}


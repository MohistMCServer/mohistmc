package com.mohistmc.mod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class SimpleLabel extends PositionedWidget {
    private final Component text;
    private final int color;

    public SimpleLabel(int relX, int relY, Component text, int color) {
        super(relX, relY, 0, 0); // 宽高可根据文本计算
        this.text = text;
        this.color = color;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.text(Minecraft.getInstance().font, text, getAbsoluteX(), getAbsoluteY(), color);
    }
}
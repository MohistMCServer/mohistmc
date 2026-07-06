package com.mohistmc.mod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class SimpleLabel extends PositionedWidget {
    private final Component text;
    private final int color;

    public SimpleLabel(int relX, int relY, Component text, int color) {
        super(relX, relY, 0, 0);
        this.text = text;
        this.color = color;
        autoSize();
    }

    /** 根据文本自动计算宽高 */
    private void autoSize() {
        try {
            var font = Minecraft.getInstance().font;
            this.width = font.width(text);
            this.height = font.lineHeight;
        } catch (Exception e) {
            this.width = 60;
            this.height = 10;
        }
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.text(Minecraft.getInstance().font, text, getAbsoluteX(), getAbsoluteY(), color);
    }
}
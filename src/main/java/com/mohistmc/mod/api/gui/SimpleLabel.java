package com.mohistmc.mod.api.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class SimpleLabel extends PositionedWidget {
    private final Component text;
    private final int color;
    private float textScale = 1.0f;

    public SimpleLabel(int relX, int relY, Component text, int color) {
        super(relX, relY, 0, 0);
        this.text = text;
        this.color = color;
        autoSize();
    }

    /**
     * 获取文字缩放比例
     */
    public float getTextScale() {
        return textScale;
    }

    /**
     * 设置文字缩放比例，<1 缩小，>1 放大
     */
    public SimpleLabel setTextScale(float scale) {
        this.textScale = Math.max(0.1f, scale);
        return this;
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
        if (Float.compare(textScale, 1.0f) == 0) {
            graphics.text(Minecraft.getInstance().font, text, getAbsoluteX(), getAbsoluteY(), color);
        } else {
            int x = getAbsoluteX();
            int y = getAbsoluteY();
            var pose = graphics.pose();
            pose.pushMatrix();
            pose.translate(x, y);
            pose.scale(textScale, textScale);
            pose.translate(-x, -y);
            graphics.text(Minecraft.getInstance().font, text, x, y, color);
            pose.popMatrix();
        }
    }
}
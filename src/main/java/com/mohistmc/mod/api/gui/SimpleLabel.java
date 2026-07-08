package com.mohistmc.mod.api.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class SimpleLabel extends PositionedWidget {
    private final Component text;
    private final int color;
    private float textScale = 1.0f;
    @Nullable
    private static Font defaultFont;
    @Nullable
    private Font customFont;

    @Nullable
    public static Font getDefaultFont() {
        return defaultFont;
    }

    /**
     * 设置全局默认字体，所有 SimpleLabel 优先使用（除非单独 setFont 覆盖）
     */
    public static void setDefaultFont(@Nullable Font font) {
        defaultFont = font;
    }

    public SimpleLabel(int relX, int relY, Component text, int color) {
        super(relX, relY, 0, 0);
        this.text = text;
        this.color = color;
        autoSize();
    }

    /**
     * 设置当前实例的自定义字体，优先级高于默认字体
     */
    public SimpleLabel setFont(@Nullable Font font) {
        this.customFont = font;
        return this;
    }

    public float getTextScale() { return textScale; }

    public SimpleLabel setTextScale(float scale) {
        this.textScale = Math.max(0.1f, scale);
        return this;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Font font = customFont != null ? customFont : defaultFont != null ? defaultFont : Minecraft.getInstance().font;
        if (Float.compare(textScale, 1.0f) == 0) {
            graphics.text(font, text, getAbsoluteX(), getAbsoluteY(), color);
        } else {
            int x = getAbsoluteX();
            int y = getAbsoluteY();
            var pose = graphics.pose();
            pose.pushMatrix();
            pose.translate(x, y);
            pose.scale(textScale, textScale);
            pose.translate(-x, -y);
            graphics.text(font, text, x, y, color);
            pose.popMatrix();
        }
    }

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
}

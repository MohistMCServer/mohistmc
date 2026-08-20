package com.mohistmc.mod.api.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 纯文本标签
 */
public class LabelItem extends ScrollListItem {
    private final Component text;
    private final int color;

    public LabelItem(Component text) {
        this.text = text;
        this.color = 0xFFFFFFFF;
    }

    public LabelItem(Component text, int color) {
        this.text = text;
        this.color = color;
    }

    @Override
    public void render(GuiGraphicsExtractor g, int x, int y, int w, boolean hovered, int alpha) {
        int c = (alpha & 0xFF000000) | (color & 0x00FFFFFF);
        g.text(font(), text, x + 4, y + (height - font().lineHeight) / 2, c);
    }
}

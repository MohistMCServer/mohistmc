package com.mohistmc.mod.api.gui;

import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 复选框项 — 文字 + 可切换的复选框
 */
@OnlyIn(Dist.CLIENT)
public class CheckboxItem extends ScrollListItem {
    private final Component label;
    private boolean checked;
    private int checkColor = 0xFF4CAF50;
    private int boxColor = 0xFF888888;
    private Consumer<Boolean> onChange;

    public CheckboxItem(Component label, boolean defaultValue) {
        this.label = label;
        this.checked = defaultValue;
    }

    public CheckboxItem onChange(Consumer<Boolean> cb) {
        this.onChange = cb;
        return this;
    }

    public CheckboxItem setCheckColor(int c) {
        this.checkColor = c;
        return this;
    }

    @Override
    public boolean handleClick(int rx, int ry, int w) {
        checked = !checked;
        if (onChange != null) onChange.accept(checked);
        return true;
    }

    @Override
    public void render(GuiGraphicsExtractor g, int x, int y, int w, boolean hovered, int alpha) {
        int fh = font().lineHeight;
        int boxSize = Math.min(height - 4, fh + 2);
        int boxX = x + w - boxSize - 6;
        int boxY = y + (height - boxSize) / 2;

        // 复选框背景
        int bg = (alpha & 0xFF000000) | (checked ? checkColor & 0x00FFFFFF : boxColor & 0x00FFFFFF);
        g.fill(boxX, boxY, boxX + boxSize, boxY + boxSize, bg);
        // 边框
        int bc = (alpha & 0xFF000000) | 0x00FFFFFF;
        g.fill(boxX, boxY, boxX + boxSize, boxY + 1, bc);
        g.fill(boxX, boxY + boxSize - 1, boxX + boxSize, boxY + boxSize, bc);
        g.fill(boxX, boxY, boxX + 1, boxY + boxSize, bc);
        g.fill(boxX + boxSize - 1, boxY, boxX + boxSize, boxY + boxSize, bc);

        // 勾号
        if (checked) {
            String tick = "✔";
            int tx = boxX + (boxSize - font().width(tick)) / 2;
            int ty = boxY + (boxSize - font().lineHeight) / 2;
            g.text(font(), tick, tx, ty, (alpha & 0xFF000000) | 0xFFFFFFFF);
        }

        // 文字
        g.text(font(), label, x + 4, y + (height - fh) / 2, (alpha & 0xFF000000) | 0xFFFFFFFF);
    }
}

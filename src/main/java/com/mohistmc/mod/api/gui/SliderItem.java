package com.mohistmc.mod.api.gui;

import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 滑动条项 — 点击轨道设置 0~1 的数值
 */
@OnlyIn(Dist.CLIENT)
public class SliderItem extends ScrollListItem {
    private final Component label;
    private float value; // 0.0 ~ 1.0
    private int trackColor = 0xFF555555;
    private int fillColor = 0xFF4CAF50;
    private int thumbColor = 0xFFFFFFFF;
    private Consumer<Float> onChange;

    public SliderItem(Component label, float defaultValue) {
        this.label = label;
        this.value = Math.max(0, Math.min(1, defaultValue));
    }

    public SliderItem onChange(Consumer<Float> cb) {
        this.onChange = cb;
        return this;
    }

    public SliderItem setFillColor(int c) {
        this.fillColor = c;
        return this;
    }

    public float getValue() {
        return value;
    }

    @Override
    public boolean handleClick(int rx, int ry, int w) {
        int labelW = font().width(label) + 10;
        int trackX = Math.max(labelW, 40);
        int trackW = w - trackX - 8;
        if (trackW <= 0) return false;
        if (rx < trackX || rx > trackX + trackW) return false;
        value = Math.max(0, Math.min(1, (float) (rx - trackX) / trackW));
        if (onChange != null) onChange.accept(value);
        return true;
    }

    @Override
    public void render(GuiGraphicsExtractor g, int x, int y, int w, boolean hovered, int alpha) {
        int fh = font().lineHeight;
        int cy = y + height / 2;
        int a = alpha & 0xFF000000;

        // 左侧标签
        g.text(font(), label, x + 4, y + (height - fh) / 2, a | 0xFFFFFFFF);

        // 轨道
        int labelW = font().width(label) + 10;
        int trackX = x + Math.max(labelW, 40);
        int trackW = w - (trackX - x) - 8;
        int trackH = 4;
        int trackY = cy - trackH / 2;

        // 轨道背景
        g.fill(trackX, trackY, trackX + trackW, trackY + trackH, a | (trackColor & 0x00FFFFFF));
        // 已填充
        int fillW = (int) (trackW * value);
        if (fillW > 0) g.fill(trackX, trackY, trackX + fillW, trackY + trackH, a | (fillColor & 0x00FFFFFF));
        // 滑块
        int thumbX = trackX + fillW - 2;
        int thumbSize = trackH + 4;
        g.fill(thumbX, cy - thumbSize / 2, thumbX + 4, cy + thumbSize / 2, a | (thumbColor & 0x00FFFFFF));

        // 百分比
        String pct = (int) (value * 100) + "%";
        g.text(font(), pct, trackX + trackW - font().width(pct), y + (height - fh) / 2, a | 0xFFAAAAAA);
    }
}

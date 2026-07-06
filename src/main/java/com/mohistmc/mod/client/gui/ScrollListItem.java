package com.mohistmc.mod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Consumer;

/**
 * 滚动列表中的自定义子项基类 — 每个子项独立控制渲染和交互。
 * 子类：{@link LabelItem}、{@link CheckboxItem}、{@link ToggleItem}、{@link SliderItem}
 */
@OnlyIn(Dist.CLIENT)
public abstract class ScrollListItem {
    protected int height = 20;
    public int getHeight() { return height; }
    public ScrollListItem setHeight(int h) { height = Math.max(8, h); return this; }

    /**
     * 处理点击
     * @param rx 点击位置相对于当前项左上角的 X
     * @param ry 点击位置相对于当前项左上角的 Y
     * @param w  项渲染宽度
     * @return true 表示消费点击
     */
    public boolean handleClick(int rx, int ry, int w) { return false; }

    /** 渲染当前项 */
    public abstract void render(GuiGraphicsExtractor g, int x, int y, int w, boolean hovered, int alpha);

    // ======== 工具 ========
    protected static Minecraft mc() { return Minecraft.getInstance(); }
    protected static net.minecraft.client.gui.Font font() { return mc().font; }
}

// ====================================================================

/** 纯文本标签 */
@OnlyIn(Dist.CLIENT)
class LabelItem extends ScrollListItem {
    private final Component text;
    private final int color;
    public LabelItem(Component text) { this.text = text; this.color = 0xFFFFFFFF; }
    public LabelItem(Component text, int color) { this.text = text; this.color = color; }
    @Override
    public void render(GuiGraphicsExtractor g, int x, int y, int w, boolean hovered, int alpha) {
        int c = (alpha & 0xFF000000) | (color & 0x00FFFFFF);
        g.text(font(), text, x + 4, y + (height - font().lineHeight) / 2, c);
    }
}

// ====================================================================

/** 复选框项 — 文字 + 可切换的复选框 */
@OnlyIn(Dist.CLIENT)
class CheckboxItem extends ScrollListItem {
    private final Component label;
    private boolean checked;
    private int checkColor = 0xFF4CAF50;
    private int boxColor = 0xFF888888;
    private Consumer<Boolean> onChange;

    public CheckboxItem(Component label, boolean defaultValue) {
        this.label = label;
        this.checked = defaultValue;
    }
    public CheckboxItem onChange(Consumer<Boolean> cb) { this.onChange = cb; return this; }
    public CheckboxItem setCheckColor(int c) { this.checkColor = c; return this; }

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

// ====================================================================

/** 切换项 — 点击循环切换预定义的选项列表 */
@OnlyIn(Dist.CLIENT)
class ToggleItem extends ScrollListItem {
    private final Component label;
    private final List<Component> optionLabels;
    private final List<Object> optionValues;
    private int selectedIndex;
    private int valueColor = 0xFFAAAAAA;
    private Consumer<Object> onChange;

    public ToggleItem(Component label, List<Component> options, int defaultIndex) {
        this.label = label;
        this.optionLabels = options;
        this.optionValues = new java.util.ArrayList<>();
        for (int i = 0; i < options.size(); i++) optionValues.add(null);
        this.selectedIndex = Math.max(0, Math.min(defaultIndex, options.size() - 1));
    }

    public ToggleItem(Component label, List<Component> optionLabels, List<Object> optionValues, int defaultIndex) {
        this.label = label;
        this.optionLabels = optionLabels;
        this.optionValues = optionValues;
        this.selectedIndex = Math.max(0, Math.min(defaultIndex, optionLabels.size() - 1));
    }

    public ToggleItem onChange(Consumer<Object> cb) { this.onChange = cb; return this; }
    public ToggleItem setValueColor(int c) { this.valueColor = c; return this; }
    public Object getValue() { return selectedIndex >= 0 && selectedIndex < optionValues.size() ? optionValues.get(selectedIndex) : null; }
    public int getSelectedIndex() { return selectedIndex; }

    @Override
    public boolean handleClick(int rx, int ry, int w) {
        selectedIndex = (selectedIndex + 1) % optionLabels.size();
        if (onChange != null) onChange.accept(getValue());
        return true;
    }

    @Override
    public void render(GuiGraphicsExtractor g, int x, int y, int w, boolean hovered, int alpha) {
        int fh = font().lineHeight;
        g.text(font(), label, x + 4, y + (height - fh) / 2, (alpha & 0xFF000000) | 0xFFFFFFFF);

        String value = optionLabels.get(selectedIndex).getString();
        String display = value + "  ›";
        int vc = (alpha & 0xFF000000) | (valueColor & 0x00FFFFFF);
        g.text(font(), display, x + w - font().width(display) - 6, y + (height - fh) / 2, vc);
    }
}

// ====================================================================

/** 滑动条项 — 点击轨道设置 0~1 的数值 */
@OnlyIn(Dist.CLIENT)
class SliderItem extends ScrollListItem {
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

    public SliderItem onChange(Consumer<Float> cb) { this.onChange = cb; return this; }
    public SliderItem setFillColor(int c) { this.fillColor = c; return this; }
    public float getValue() { return value; }

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

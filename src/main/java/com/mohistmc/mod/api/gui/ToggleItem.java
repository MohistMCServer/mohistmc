package com.mohistmc.mod.api.gui;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 切换项 — 点击循环切换预定义的选项列表
 */
@OnlyIn(Dist.CLIENT)
public class ToggleItem extends ScrollListItem {
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

    public ToggleItem onChange(Consumer<Object> cb) {
        this.onChange = cb;
        return this;
    }

    public ToggleItem setValueColor(int c) {
        this.valueColor = c;
        return this;
    }

    public Object getValue() {
        return selectedIndex >= 0 && selectedIndex < optionValues.size() ? optionValues.get(selectedIndex) : null;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

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

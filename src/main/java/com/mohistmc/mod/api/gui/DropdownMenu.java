package com.mohistmc.mod.api.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 下拉菜单组件 — 点击触发展开选项列表，支持链式配置
 */
@OnlyIn(Dist.CLIENT)
public class DropdownMenu<T> extends PositionedWidget {

    private Component label;
    private int triggerColor;
    private int triggerHoverColor;

    private final List<T> values = new ArrayList<>();
    private final List<Component> displayLabels = new ArrayList<>();
    private int selectedIndex = -1;

    private int itemHeight = 16;
    private int maxVisibleItems = 8;
    private boolean expanded;
    private int hoveredIndex = -1;

    private int textColor = 0xFFFFFFFF;
    private int hoverItemColor = 0xFF555555;
    private int dropdownBgColor = 0xFF333333;
    private int borderColor = 0xFF888888;

    private Consumer<T> onSelect;

    // ======== 构造 ========

    public DropdownMenu(int relX, int relY, int width, int height, Component label, int bgColor) {
        super(relX, relY, width, height);
        this.label = label;
        this.triggerColor = bgColor;
        this.triggerHoverColor = darken(bgColor, 0.75f);
    }

    // ======== 链式配置 ========

    /** 添加一个选项 */
    public DropdownMenu<T> addOption(T value, Component display) {
        values.add(value);
        displayLabels.add(display);
        return this;
    }

    /** 设置默认选中项索引（-1 = 不选中） */
    public DropdownMenu<T> setSelectedIndex(int index) {
        this.selectedIndex = (index >= 0 && index < values.size()) ? index : -1;
        return this;
    }

    /** 每项高度 */
    public DropdownMenu<T> setItemHeight(int height) {
        this.itemHeight = Math.max(10, height);
        return this;
    }

    /** 最大可见项数（超出滚动） */
    public DropdownMenu<T> setMaxVisibleItems(int max) {
        this.maxVisibleItems = Math.max(3, max);
        return this;
    }

    /** 文字颜色 */
    public DropdownMenu<T> setTextColor(int color) {
        this.textColor = color;
        return this;
    }

    /** 悬停项背景色 */
    public DropdownMenu<T> setHoverItemColor(int color) {
        this.hoverItemColor = color;
        return this;
    }

    /** 下拉列表背景色 */
    public DropdownMenu<T> setDropdownBgColor(int color) {
        this.dropdownBgColor = color;
        return this;
    }

    /** 边框颜色 */
    public DropdownMenu<T> setBorderColor(int color) {
        this.borderColor = color;
        return this;
    }

    /** 选中回调 */
    public DropdownMenu<T> onSelect(Consumer<T> callback) {
        this.onSelect = callback;
        return this;
    }

    @Override
    public DropdownMenu<T> setRightAnchored(boolean anchored) {
        super.setRightAnchored(anchored);
        return this;
    }

    @Override
    public DropdownMenu<T> setBottomAnchored(boolean anchored) {
        super.setBottomAnchored(anchored);
        return this;
    }

    @Override
    public DropdownMenu<T> setAlpha(int alpha) {
        super.setAlpha(alpha);
        return this;
    }

    @Override
    public DropdownMenu<T> setAlpha(float alpha) {
        super.setAlpha(alpha);
        return this;
    }

    // ======== 查询 ========

    /** 当前选中值 */
    public T getSelectedValue() {
        return selectedIndex >= 0 && selectedIndex < values.size() ? values.get(selectedIndex) : null;
    }

    /** 是否处于展开状态 */
    public boolean isExpanded() {
        return expanded;
    }

    /** 展开/收起 */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        if (!expanded) hoveredIndex = -1;
    }

    /** 下拉列表占据的总高度 */
    public int getDropdownHeight() {
        int count = Math.min(values.size(), maxVisibleItems);
        return count * itemHeight + 1; // +1 边框
    }

    // ======== 点击处理 ========

    boolean handleClick(MouseButtonEvent event, boolean doubleClick) {
        int mx = logicalX(event);
        int my = logicalY(event);
        int tx = getAbsoluteX();
        int ty = getAbsoluteY();

        // 1) 点击触发器 → 切换展开
        if (mx >= tx && mx < tx + width && my >= ty && my < ty + height) {
            expanded = !expanded;
            hoveredIndex = -1;
            return true;
        }

        // 2) 已展开：点击选项 → 选中并收起
        if (expanded) {
            int dy = ty + height + 1;
            int dH = getDropdownHeight();
            if (mx >= tx && mx < tx + width && my >= dy && my < dy + dH) {
                int index = (my - dy) / itemHeight;
                if (index < values.size()) {
                    selectedIndex = index;
                    if (onSelect != null) {
                        onSelect.accept(values.get(index));
                    }
                }
                expanded = false;
                hoveredIndex = -1;
                return true;
            }
            // 3) 点击外部 → 收起
            expanded = false;
            hoveredIndex = -1;
        }

        return false;
    }

    // ======== 渲染 ========

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = getAbsoluteX();
        int y = getAbsoluteY();
        boolean hovered = isMouseOver(mouseX, mouseY);

        // — 触发器 —
        int bg = applyAlpha(hovered ? triggerHoverColor : triggerColor);
        graphics.fill(x, y, x + width, y + height, bg);

        // 触发器边框
        graphics.fill(x, y, x + width, y + 1, applyAlpha(borderColor));
        graphics.fill(x, y + height - 1, x + width, y + height, applyAlpha(borderColor));
        graphics.fill(x, y, x + 1, y + height, applyAlpha(borderColor));
        graphics.fill(x + width - 1, y, x + width, y + height, applyAlpha(borderColor));

        // 文字（选中的值优先，否则显示 label）
        var font = Minecraft.getInstance().font;
        Component display = selectedIndex >= 0 ? displayLabels.get(selectedIndex) : label;
        int textX = x + 4;
        int textY = y + (height - font.lineHeight) / 2;
        graphics.text(font, display, textX, textY, applyAlpha(textColor));

        // 箭头 ▼ / ▲
        String arrow = expanded ? "▲" : "▼";
        int arrowX = x + width - font.width(arrow) - 4;
        graphics.text(font, arrow, arrowX, textY, applyAlpha(textColor));

        // — 下拉列表 —
        if (expanded && !values.isEmpty()) {
            int dy = y + height + 1;
            int itemW = width;
            int visibleCount = Math.min(values.size(), maxVisibleItems);
            int dh = visibleCount * itemHeight;

            // 列表背景 + 边框
            int dbg = applyAlpha(dropdownBgColor);
            int dbc = applyAlpha(borderColor);
            graphics.fill(x, dy, x + itemW, dy + dh, dbg);
            graphics.fill(x, dy, x + itemW, dy + 1, dbc);       // 上
            graphics.fill(x, dy + dh - 1, x + itemW, dy + dh, dbc); // 下
            graphics.fill(x, dy, x + 1, dy + dh, dbc);          // 左
            graphics.fill(x + itemW - 1, dy, x + itemW, dy + dh, dbc); // 右

            // 更新悬停索引（仅用于视觉）
            hoveredIndex = -1;
            if (mouseX >= x && mouseX < x + itemW && mouseY >= dy && mouseY < dy + dh) {
                hoveredIndex = Math.min((mouseY - dy) / itemHeight, values.size() - 1);
            }

            // 选项
            for (int i = 0; i < visibleCount; i++) {
                int iy = dy + i * itemHeight;
                if (i == hoveredIndex) {
                    graphics.fill(x + 1, iy, x + itemW - 1, iy + itemHeight, applyAlpha(hoverItemColor));
                }
                if (i == selectedIndex) {
                    // 选中项加一个小勾 ✓
                    Component optText = Component.literal("✓ ").append(displayLabels.get(i));
                    graphics.text(font, optText, x + 4, iy + (itemHeight - font.lineHeight) / 2, applyAlpha(0xFFFFFFAA));
                } else {
                    graphics.text(font, displayLabels.get(i), x + 4, iy + (itemHeight - font.lineHeight) / 2, applyAlpha(textColor));
                }
            }

            // 如果有更多项但被截断，显示滚动提示
            if (values.size() > maxVisibleItems) {
                String more = "⋯ +" + (values.size() - maxVisibleItems);
                int moreY = dy + dh - font.lineHeight - 2;
                graphics.text(font, more, x + itemW - font.width(more) - 4, moreY, applyAlpha(0xFF888888));
            }
        }
    }

    // ======== 工具 ========

    private static int darken(int color, float factor) {
        int a = color >>> 24;
        int r = (int) (((color >> 16) & 0xFF) * factor);
        int g = (int) (((color >> 8) & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}

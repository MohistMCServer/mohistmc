package com.mohistmc.mod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 滚动列表组件 — 支持自定义子项（LabelItem / CheckboxItem / ToggleItem / SliderItem）。
 *
 * <pre>
 *   var list = new ScrollList(x, y, 180, 120, 0xFF2D2D2D);
 *   list.addItem(new LabelItem(Component.literal("标题")));
 *   list.addItem(new CheckboxItem(Component.literal("启用"), true).onChange(v -> {}));
 *   list.addItem(new ToggleItem(Component.literal("模式"), List.of(Component.literal("A"), Component.literal("B")), 0));
 *   list.addItem(new SliderItem(Component.literal("音量"), 0.8f).onChange(v -> {}));
 * </pre>
 */
@OnlyIn(Dist.CLIENT)
public class ScrollList extends PositionedWidget {

    // ======== 子项数据 ========
    private final List<ScrollListItem> slotItems = new ArrayList<>();

    // ======== 样式 ========
    private int bgColor = 0xFF2D2D2D;
    private int hoverColor = 0x33FFFFFF;
    private int scrollbarColor = 0xFF555555;
    private int scrollbarHoverColor = 0xFF777777;
    private int scrollbarBg = 0x33FFFFFF;
    private int scrollbarWidth = 3;
    private int borderColor;
    private int borderWidth;

    // ======== 状态 ========
    private int scrollOffset;
    private int hoveredIndex = -1;
    private boolean scrollbarHovered;
    private int dragItemIndex = -1; // 正在拖拽的子项索引（-1=无）

    // ======== 构造 ========

    public ScrollList(int relX, int relY, int width, int height) {
        super(relX, relY, Math.max(40, width), Math.max(20, height));
        this.bgColor = 0xFF2D2D2D;
    }

    public ScrollList(int relX, int relY, int width, int height, int bgColor) {
        super(relX, relY, Math.max(40, width), Math.max(20, height));
        this.bgColor = bgColor;
    }

    // ======== 链式配置 ========

    @Override public ScrollList setFlexGrow(int grow) { super.setFlexGrow(grow); return this; }
    @Override public ScrollList setRightAnchored(boolean a) { super.setRightAnchored(a); return this; }
    @Override public ScrollList setBottomAnchored(boolean a) { super.setBottomAnchored(a); return this; }
    @Override public ScrollList setAlpha(int alpha) { super.setAlpha(alpha); return this; }
    @Override public ScrollList setAlpha(float alpha) { super.setAlpha(alpha); return this; }

    /** 添加自定义子项 */
    public ScrollList addItem(ScrollListItem item) { slotItems.add(item); return this; }
    /** 添加纯文本标签（快捷方式） */
    public ScrollList addItem(Component text) { slotItems.add(new LabelItem(text)); return this; }
    public ScrollList setHoverColor(int c) { this.hoverColor = c; return this; }
    public ScrollList setScrollbarColor(int c) { this.scrollbarColor = c; return this; }
    public ScrollList setBorder(int color, int width) { this.borderColor = color; this.borderWidth = Math.max(0, width); return this; }

    // ======== 查询 ========

    /** 内容总高度（所有子项高度之和） */
    public int getContentHeight() {
        int total = 0;
        for (var item : slotItems) total += item.getHeight();
        return total;
    }
    /** 可见内容区高度 */
    public int getViewportHeight() { return height - borderWidth * 2; }
    /** 最大滚动偏移 */
    public int getMaxScroll() { return Math.max(0, getContentHeight() - getViewportHeight()); }

    // ======== 滚轮 ========

    boolean handleScroll(double mouseX, double mouseY, double delta) {
        int ax = getAbsoluteX();
        int ay = getAbsoluteY();
        if (mouseX < ax || mouseX >= ax + width || mouseY < ay || mouseY >= ay + height) return false;
        int old = scrollOffset;
        scrollOffset = (int) Math.max(0, Math.min(getMaxScroll(), scrollOffset - delta * 20));
        return scrollOffset != old;
    }

    // ======== 点击 ========

    boolean handleClick(MouseButtonEvent event, boolean doubleClick) {
        int mx = (int) event.x();
        int my = (int) event.y();
        int bx = getAbsoluteX() + borderWidth;
        int by = getAbsoluteY() + borderWidth;
        int vw = width - borderWidth * 2;
        int vh = getViewportHeight();

        if (mx < bx || mx >= bx + vw || my < by || my >= by + vh) return false;

        // 滚动条
        int sbX = bx + vw - scrollbarWidth;
        if (mx >= sbX && mx < sbX + scrollbarWidth && getContentHeight() > vh) {
            float ratio = (float) (my - by) / vh;
            scrollOffset = (int) (ratio * getContentHeight() - vh / 2f);
            scrollOffset = Math.max(0, Math.min(getMaxScroll(), scrollOffset));
            return true;
        }

        // 查找被点击的项
        int relY = my - by + scrollOffset;
        int accY = 0;
        for (int i = 0; i < slotItems.size(); i++) {
            var item = slotItems.get(i);
            int itemH = item.getHeight();
            if (relY >= accY && relY < accY + itemH) {
                int rx = mx - bx;
                int ry = relY - accY;
                if (item instanceof SliderItem || item instanceof CheckboxItem || item instanceof ToggleItem) {
                    dragItemIndex = i; // 记录拖拽起始
                }
                return item.handleClick(rx, ry, vw);
            }
            accY += itemH;
        }
        return true;
    }

    /** 处理鼠标拖拽（由 EnhancedScreen 转发） */
    void handleDrag(int mx, int my) {
        if (dragItemIndex < 0 || dragItemIndex >= slotItems.size()) return;
        int bx = getAbsoluteX() + borderWidth;
        int by = getAbsoluteY() + borderWidth;
        int vw = width - borderWidth * 2;
        var item = slotItems.get(dragItemIndex);
        if (item instanceof SliderItem slider) {
            int rx = mx - bx;
            int ry = my - by;
            slider.handleClick(rx, ry, vw);
        }
    }

    /** 释放拖拽 */
    void handleRelease() {
        dragItemIndex = -1;
    }

    /** 更新悬停 */
    private void updateHover(int mx, int my) {
        hoveredIndex = -1;
        scrollbarHovered = false;
        int absX = getAbsoluteX();
        int absY = getAbsoluteY();
        if (mx < absX || mx >= absX + width || my < absY || my >= absY + height) return;
        int x = absX + borderWidth;
        int y = absY + borderWidth;
        int vw = width - borderWidth * 2;
        int vh = getViewportHeight();
        if (mx < x || mx >= x + vw || my < y || my >= y + vh) return;

        hoveredIndex = getItemAt(my - y + scrollOffset);
        int sbX = x + vw - scrollbarWidth;
        scrollbarHovered = (mx >= sbX && mx < sbX + scrollbarWidth);
    }

    private int getItemAt(int relY) {
        int accY = 0;
        for (int i = 0; i < slotItems.size(); i++) {
            accY += slotItems.get(i).getHeight();
            if (relY < accY) return i;
        }
        return -1;
    }

    // ======== 渲染 ========

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = getAbsoluteX() + borderWidth;
        int y = getAbsoluteY() + borderWidth;
        int vw = width - borderWidth * 2;
        int vh = getViewportHeight();

        // 1) 背景 + 边框
        if (borderWidth > 0 && borderColor != 0) {
            graphics.fill(getAbsoluteX(), getAbsoluteY(), getAbsoluteX() + width, getAbsoluteY() + height, applyAlpha(borderColor));
            graphics.fill(x, y, x + vw, y + vh, applyAlpha(bgColor));
        } else {
            graphics.fill(x, y, x + vw, y + vh, applyAlpha(bgColor));
        }

        if (slotItems.isEmpty()) return;

        updateHover(mouseX, mouseY);
        int al = applyAlpha(0xFFFFFFFF);
        int a = (al & 0xFF000000);
        boolean needScroll = getContentHeight() > vh;
        int scrollReserve = needScroll ? scrollbarWidth + 2 : 0; // 为滚动条预留空间
        int itemW = vw - scrollReserve;

        // 2) 可见项
        int startY = scrollOffset;
        int endY = scrollOffset + vh;
        int curY = y;
        for (int i = 0; i < slotItems.size(); i++) {
            var item = slotItems.get(i);
            int itemH = item.getHeight();
            int itemTop = curY - scrollOffset;
            int itemBottom = itemTop + itemH;

            // 跳过任何一部分超出视口的项（防止无裁剪溢出上下边界）
            if (itemTop >= y + vh) break;
            if (itemTop < y || itemBottom > y + vh) {
                curY += itemH;
                continue;
            }

            // 渲染
            boolean hover = i == hoveredIndex;
            if (hover) {
                graphics.fill(x, itemTop, x + itemW, itemTop + itemH, applyAlpha(hoverColor));
            }
            item.render(graphics, x, itemTop, itemW, hover, al);
            curY += itemH;
        }

        // 3) 滚动条（右侧独立区域，不覆盖 item）
        if (needScroll) {
            int sbX = x + vw - scrollbarWidth;
            float ratio = (float) vh / getContentHeight();
            int sbH = Math.max(8, (int) (vh * ratio));
            int trackH = vh - sbH;
            int sbY = y + (getMaxScroll() > 0 ? (int) ((float) scrollOffset / getMaxScroll() * trackH) : 0);
            int sc = scrollbarHovered ? scrollbarHoverColor : scrollbarColor;
            graphics.fill(sbX, sbY, sbX + scrollbarWidth, sbY + sbH, a | (sc & 0x00FFFFFF));
        }
    }
}

package com.mohistmc.mod.api.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 单列滚动列表 — 继承 {@link ScrollableWidget} 自动获得
 * 滚轮/滚动条点击跳转/滚动条长按拖拽/平滑滚动/scissor 裁剪/内容内边距，
 * 本类只负责单列条目布局与交互（点击定位、悬停、滑块子项拖拽）。
 *
 * <pre>
 *   var list = new ScrollList(x, y, 180, 120, 0xFF2D2D2D);
 *   list.addItem(new LabelItem(Component.literal("标题")));
 *   list.addItem(new CheckboxItem(Component.literal("启用"), true).onChange(v -> {}));
 *   list.addItem(new ToggleItem(Component.literal("模式"), List.of(Component.literal("A"), Component.literal("B")), 0));
 *   list.addItem(new SliderItem(Component.literal("音量"), 0.8f).onChange(v -> {}));
 * </pre>
 */
public class ScrollList extends ScrollableWidget {

    // ======== 子项数据 ========
    private final List<ScrollListItem> slotItems = new ArrayList<>();

    // ======== 构造 ========

    public ScrollList(int relX, int relY, int width, int height) {
        super(relX, relY, width, height);
    }

    public ScrollList(int relX, int relY, int width, int height, int bgColor) {
        super(relX, relY, width, height);
        this.bgColor = bgColor;
    }

    // ======== 链式配置（返回自身类型） ========

    @Override public ScrollList setFlexGrow(int grow) { super.setFlexGrow(grow); return this; }
    @Override public ScrollList setRightAnchored(boolean a) { super.setRightAnchored(a); return this; }
    @Override public ScrollList setBottomAnchored(boolean a) { super.setBottomAnchored(a); return this; }
    @Override public ScrollList setAlpha(int alpha) { super.setAlpha(alpha); return this; }
    @Override public ScrollList setAlpha(float alpha) { super.setAlpha(alpha); return this; }
    @Override public ScrollList setHoverColor(int c) { super.setHoverColor(c); return this; }
    @Override public ScrollList setScrollbarColor(int c) { super.setScrollbarColor(c); return this; }
    @Override public ScrollList setBorder(int color, int width) { super.setBorder(color, width); return this; }
    @Override public ScrollList setContentPadding(int padding) { super.setContentPadding(padding); return this; }

    /** 获取所有子项（外部只读遍历） */
    public List<ScrollListItem> getItems() { return slotItems; }

    /** 清空所有子项（滚动位置归零） */
    public void clearItems() { slotItems.clear(); scrollOffset = 0; targetScrollOffset = 0; }

    /** 添加自定义子项 */
    public ScrollList addItem(ScrollListItem item) { slotItems.add(item); return this; }
    /** 添加纯文本标签（快捷方式） */
    public ScrollList addItem(Component text) { slotItems.add(new LabelItem(text)); return this; }

    // ======== 内容 ========

    /** 内容总高度（所有子项高度之和），兼容旧调用 */
    public int getContentHeight() { return getContentSize(); }

    @Override
    public int getContentSize() {
        int total = 0;
        for (var item : slotItems) total += item.getHeight();
        return total;
    }

    // ======== 内容交互 ========

    @Override
    protected boolean handleContentClick(MouseButtonEvent event, int bx, int by, int vw, int vh) {
        int mx = logicalX(event);
        int my = logicalY(event);
        int relY = my - by + scrollOffset - getContentPadding();
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

    @Override
    protected void updateItemHover(int mx, int my) {
        hoveredIndex = -1;
        int absX = getAbsoluteX();
        int absY = getAbsoluteY();
        if (mx < absX || mx >= absX + width || my < absY || my >= absY + height) return;
        int x = absX + getBorderWidth();
        int y = absY + getBorderWidth();
        int vw = getViewportWidth();
        int vh = getViewportHeight();
        if (mx < x || mx >= x + vw || my < y || my >= y + vh) return;
        hoveredIndex = getItemAt(my - y + scrollOffset - getContentPadding());
    }

    private int getItemAt(int relY) {
        int accY = 0;
        for (int i = 0; i < slotItems.size(); i++) {
            accY += slotItems.get(i).getHeight();
            if (relY < accY) return i;
        }
        return -1;
    }

    @Override
    protected void onDragContent(int mx, int my) {
        if (dragItemIndex < 0 || dragItemIndex >= slotItems.size()) return;
        int bx = getAbsoluteX() + getBorderWidth();
        int by = getAbsoluteY() + getBorderWidth();
        int vw = getViewportWidth();
        var item = slotItems.get(dragItemIndex);
        if (item instanceof SliderItem slider) {
            slider.handleClick(mx - bx, my - by, vw);
        }
    }

    // ======== 渲染 ========

    @Override
    protected void renderItems(GuiGraphicsExtractor g, int x, int y, int vw, int vh, int alpha) {
        int pad = getContentPadding();
        int scrollReserve = needScroll() ? getScrollbarWidth() + 2 : 0; // 为滚动条预留空间
        int itemW = vw - scrollReserve;
        int contentTop = y + pad;
        int contentBottom = y + vh - pad;

        int curY = contentTop;
        for (int i = 0; i < slotItems.size(); i++) {
            var item = slotItems.get(i);
            int itemH = item.getHeight();
            int itemTop = curY - scrollOffset;

            if (itemTop >= contentBottom) break;      // 完全在视口下方，终止
            if (itemTop + itemH <= contentTop) {      // 完全在视口上方，跳过
                curY += itemH;
                continue;
            }

            // 渲染（部分可见项由基类 scissor 裁剪）
            boolean hover = i == hoveredIndex;
            if (hover && item.renderHoverOverlay()) {
                fillHover(g, x, itemTop, itemW, itemH, hover);
            }
            item.render(g, x, itemTop, itemW, hover, alpha);
            curY += itemH;
        }
    }
}

package com.mohistmc.mod.api.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 多列卡片网格滚动容器 — 继承 {@link ScrollableWidget} 自动获得
 * 滚轮/滚动条点击跳转/滚动条长按拖拽/平滑滚动/scissor 裁剪，
 * 本类只负责 按 列×行 网格布局子项。
 * <p>注意：网格模式要求所有子项等高（卡片场景成立），列宽由容器自动均分。
 *
 * <pre>
 *   var grid = new GridScrollList(x, y, 360, 240, 0x66000000);
 *   grid.setColumns(3).setGap(8, 8);
 *   grid.addItem(new ShopCard(...));
 * </pre>
 */
@OnlyIn(Dist.CLIENT)
public class GridScrollList extends ScrollableWidget {

    // ======== 子项数据 ========
    private final List<ScrollListItem> slotItems = new ArrayList<>();

    // ======== 布局 ========
    private int columns = 2;
    private int hGap = 8;
    private int vGap = 8;
    /** 正方形单元格模式：卡片高 = 列宽（忽略子项自身高度） */
    private boolean squareCells;
    /** 单元格附加高度（正方形模式下卡片下方的附加区，如价格徽章行） */
    private int cellExtraHeight;
    /** 内容内边距（卡片与背景框边缘的间距，四边一致） */
    private int padding;

    // ======== 构造 ========

    public GridScrollList(int relX, int relY, int width, int height, int bgColor) {
        super(relX, relY, width, height);
        this.bgColor = bgColor;
    }

    // ======== 链式配置（返回自身类型） ========

    @Override public GridScrollList setFlexGrow(int grow) { super.setFlexGrow(grow); return this; }
    @Override public GridScrollList setRightAnchored(boolean a) { super.setRightAnchored(a); return this; }
    @Override public GridScrollList setBottomAnchored(boolean a) { super.setBottomAnchored(a); return this; }
    @Override public GridScrollList setAlpha(int alpha) { super.setAlpha(alpha); return this; }
    @Override public GridScrollList setAlpha(float alpha) { super.setAlpha(alpha); return this; }
    @Override public GridScrollList setHoverColor(int c) { super.setHoverColor(c); return this; }
    @Override public GridScrollList setScrollbarColor(int c) { super.setScrollbarColor(c); return this; }
    @Override public GridScrollList setBorder(int color, int width) { super.setBorder(color, width); return this; }

    /** 网格列数（>=1） */
    public GridScrollList setColumns(int cols) { this.columns = Math.max(1, cols); return this; }
    /** 卡片水平/垂直间距 */
    public GridScrollList setGap(int hGap, int vGap) { this.hGap = Math.max(0, hGap); this.vGap = Math.max(0, vGap); return this; }
    /** 正方形单元格模式：卡片高 = 列宽，子项自身高度被忽略 */
    public GridScrollList setSquareCells(boolean square) { this.squareCells = square; return this; }
    /** 单元格附加高度（卡片下方附加区，如徽章行；仅正方形模式生效） */
    public GridScrollList setCellExtraHeight(int height) { this.cellExtraHeight = Math.max(0, height); return this; }
    /** 内容内边距（卡片与背景框边缘的间距） */
    public GridScrollList setPadding(int padding) { this.padding = Math.max(0, padding); return this; }

    public List<ScrollListItem> getItems() { return slotItems; }

    /** 当前悬停的子项索引（-1 = 无），供外部显示 tooltip 等 */
    @Override public int getHoveredIndex() { return hoveredIndex; }

    public void clearItems() { slotItems.clear(); scrollOffset = 0; targetScrollOffset = 0; }

    public GridScrollList addItem(ScrollListItem item) { slotItems.add(item); return this; }

    // ======== 布局计算 ========

    /** 是否需要滚动条（正方形模式下用 item 高度近似估算，避免与列宽递归） */
    private boolean needScrollInternal() {
        if (!squareCells) return getContentSize() > getScrollViewportSize();
        int rows = getRowCount();
        if (rows == 0) return false;
        int estCardH = (getViewportWidth() - padding * 2 - (columns - 1) * hGap) / columns;
        return rows * (estCardH + cellExtraHeight) + (rows - 1) * vGap > getScrollViewportSize();
    }

    /** 当前列宽（视口宽 - 内边距 - 滚动条预留 - 列间距 后均分） */
    public int getCardWidth() {
        int vw = getViewportWidth() - padding * 2;
        int scrollReserve = needScrollInternal() ? getScrollbarWidth() + 2 : 0;
        return (vw - scrollReserve - (columns - 1) * hGap) / columns;
    }

    /** 统一卡片高（网格要求等高；正方形模式取列宽，否则取第一项，无项时为 0） */
    private int getCardHeight() {
        if (squareCells) return getCardWidth();
        return slotItems.isEmpty() ? 0 : slotItems.get(0).getHeight();
    }

    private int getRowCount() {
        return (slotItems.size() + columns - 1) / columns;
    }

    /** 单行高度（卡片 + 附加区 + 垂直间距） */
    private int getRowHeight() {
        return getCardHeight() + cellExtraHeight + vGap;
    }

    /** 内容总高度（行数 × 行高），兼容旧调用 */
    public int getContentHeight() { return getContentSize(); }

    @Override
    public int getContentSize() {
        int rows = getRowCount();
        if (rows == 0) return 0;
        return rows * getRowHeight() - vGap;
    }

    // ======== 内容交互 ========

    @Override
    protected boolean handleContentClick(MouseButtonEvent event, int bx, int by, int vw, int vh) {
        int mx = (int) event.x();
        int my = (int) event.y();
        int cardW = getCardWidth();
        int cardH = getCardHeight();
        if (cardW <= 0 || cardH <= 0) return true;

        int relX = mx - bx - padding;
        int relY = my - by - padding + scrollOffset;
        if (relX < 0 || relX >= vw - padding * 2) return true;
        int rowH = cardH + cellExtraHeight + vGap;
        int row = relY / rowH;
        int col = relX / (cardW + hGap);
        if (col >= columns) return true;
        int index = row * columns + col;
        if (index < 0 || index >= slotItems.size()) return true;

        int rx = relX - col * (cardW + hGap);
        int ry = relY - row * rowH;
        return slotItems.get(index).handleClick(rx, ry, cardW);
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

        int cardW = getCardWidth();
        int cardH = getCardHeight();
        if (cardW <= 0 || cardH <= 0) return;
        int relX = mx - x - padding;
        int relY = my - y - padding + scrollOffset;
        if (relX < 0 || relX >= vw - padding * 2) return;
        int rowH = cardH + cellExtraHeight + vGap;
        int row = relY / rowH;
        int col = relX / (cardW + hGap);
        if (col >= columns) return;
        int index = row * columns + col;
        if (index >= 0 && index < slotItems.size()) {
            hoveredIndex = index;
        }
    }

    // ======== 渲染 ========

    @Override
    protected void renderItems(GuiGraphicsExtractor g, int x, int y, int vw, int vh, int alpha) {
        int cardW = getCardWidth();
        int cardH = getCardHeight();
        if (cardW <= 0 || cardH <= 0) return;

        // 网格内容裁剪到内边距带内（基类已开视口 scissor，此处收紧到 padding 带）
        g.enableScissor(x + padding, y + padding, x + vw - padding, y + vh - padding);
        int rowH = cardH + cellExtraHeight + vGap;
        int startRow = Math.max(0, scrollOffset / rowH - 1);
        int endRow = Math.min(getRowCount() - 1, (scrollOffset + vh) / rowH + 1);

        for (int row = startRow; row <= endRow; row++) {
            int cardTop = y + padding + row * rowH - scrollOffset;
            if (cardTop + cardH <= y || cardTop >= y + vh) continue;
            for (int col = 0; col < columns; col++) {
                int index = row * columns + col;
                if (index >= slotItems.size()) break;
                int cardX = x + padding + col * (cardW + hGap);
                boolean hover = index == hoveredIndex;
                if (hover) {
                    fillHover(g, cardX, cardTop, cardW, cardH, hover);
                }
                slotItems.get(index).render(g, cardX, cardTop, cardW, hover, alpha);
            }
        }
        g.disableScissor();
    }
}

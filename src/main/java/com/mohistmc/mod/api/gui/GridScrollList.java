package com.mohistmc.mod.api.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 多列卡片网格滚动容器 — 与 {@link ScrollList} 同套滚动/裁剪/滚动条机制，按 列×行 网格布局子项。
 * <p>注意：网格模式要求所有子项等高（卡片场景成立），列宽由容器自动均分。
 *
 * <pre>
 *   var grid = new GridScrollList(x, y, 360, 240, 0x66000000);
 *   grid.setColumns(3).setGap(8, 8);
 *   grid.addItem(new ShopCard(...));
 * </pre>
 */
@OnlyIn(Dist.CLIENT)
public class GridScrollList extends PositionedWidget {

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
    /** 平滑滚动目标值（滚轮/滚动条只改目标，每帧插值逼近） */
    private int targetScrollOffset;
    /** 正在拖动滚动条 */
    private boolean draggingScrollbar;
    private int hoveredIndex = -1;
    private boolean scrollbarHovered;

    // ======== 构造 ========

    public GridScrollList(int relX, int relY, int width, int height, int bgColor) {
        super(relX, relY, Math.max(40, width), Math.max(20, height));
        this.bgColor = bgColor;
    }

    // ======== 链式配置 ========

    @Override public GridScrollList setFlexGrow(int grow) { super.setFlexGrow(grow); return this; }
    @Override public GridScrollList setRightAnchored(boolean a) { super.setRightAnchored(a); return this; }
    @Override public GridScrollList setBottomAnchored(boolean a) { super.setBottomAnchored(a); return this; }
    @Override public GridScrollList setAlpha(int alpha) { super.setAlpha(alpha); return this; }
    @Override public GridScrollList setAlpha(float alpha) { super.setAlpha(alpha); return this; }

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
    public GridScrollList setHoverColor(int c) { this.hoverColor = c; return this; }
    public GridScrollList setScrollbarColor(int c) { this.scrollbarColor = c; return this; }
    public GridScrollList setBorder(int color, int width) { this.borderColor = color; this.borderWidth = Math.max(0, width); return this; }

    public List<ScrollListItem> getItems() { return slotItems; }

    /** 当前悬停的子项索引（-1 = 无），供外部显示 tooltip 等 */
    public int getHoveredIndex() { return hoveredIndex; }

    public void clearItems() { slotItems.clear(); scrollOffset = 0; targetScrollOffset = 0; }

    public GridScrollList addItem(ScrollListItem item) { slotItems.add(item); return this; }

    // ======== 布局计算 ========

    /** 可见内容区宽 */
    private int getViewportWidth() { return width - borderWidth * 2; }
    /** 可见内容区高 */
    private int getViewportHeight() { return height - borderWidth * 2; }
    /** 内容滚动可用高（视口高减上下内边距，滚动/滚动条均以此为准） */
    private int getScrollViewportHeight() { return Math.max(0, getViewportHeight() - padding * 2); }

    /** 是否需要滚动条（正方形模式下用 item 高度近似估算，避免与列宽递归） */
    private boolean needScrollInternal() {
        if (!squareCells) return getContentHeight() > getScrollViewportHeight();
        int rows = getRowCount();
        if (rows == 0) return false;
        int estCardH = (getViewportWidth() - padding * 2 - (columns - 1) * hGap) / columns;
        return rows * (estCardH + cellExtraHeight) + (rows - 1) * vGap > getScrollViewportHeight();
    }

    /** 当前列宽（视口宽 - 内边距 - 滚动条预留 - 列间距 后均分） */
    public int getCardWidth() {
        int vw = getViewportWidth() - padding * 2;
        int scrollReserve = needScrollInternal() ? scrollbarWidth + 2 : 0;
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

    /** 内容总高度（行数 × 行高） */
    public int getContentHeight() {
        int rows = getRowCount();
        if (rows == 0) return 0;
        return rows * getRowHeight() - vGap;
    }

    /** 最大滚动偏移（内容底对齐滚动区底，上下各留 padding 边距） */
    public int getMaxScroll() { return Math.max(0, getContentHeight() - getScrollViewportHeight()); }

    // ======== 滚轮 ========

    boolean handleScroll(double mouseX, double mouseY, double delta) {
        int ax = getAbsoluteX();
        int ay = getAbsoluteY();
        if (mouseX < ax || mouseX >= ax + width || mouseY < ay || mouseY >= ay + height) return false;
        int old = targetScrollOffset;
        targetScrollOffset = (int) Math.max(0, Math.min(getMaxScroll(), targetScrollOffset - delta * 20));
        return targetScrollOffset != old;
    }

    // ======== 点击 ========

    boolean handleClick(MouseButtonEvent event, boolean doubleClick) {
        int mx = (int) event.x();
        int my = (int) event.y();
        int bx = getAbsoluteX() + borderWidth;
        int by = getAbsoluteY() + borderWidth;
        int vw = getViewportWidth();
        int vh = getViewportHeight();

        if (mx < bx || mx >= bx + vw || my < by || my >= by + vh) return false;

        // 滚动条（轨道为减去上下内边距后的有效区）：按下开始拖动（长按自由拖动）
        int sbX = bx + vw - scrollbarWidth;
        int scrollVh = getScrollViewportHeight();
        if (mx >= sbX && mx < sbX + scrollbarWidth && getContentHeight() > scrollVh) {
            draggingScrollbar = true;
            updateScrollbarFromMouse(my);
            return true;
        }

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

    /** 滚动条拖拽（由 EnhancedScreen 转发）：长按自由拖动 */
    void handleDrag(int mx, int my) {
        if (draggingScrollbar) {
            updateScrollbarFromMouse(my);
        }
    }

    /** 释放拖拽 */
    void handleRelease() {
        draggingScrollbar = false;
    }

    /** 按鼠标 Y 更新滚动条目标位置（thumb 中心跟随鼠标） */
    private void updateScrollbarFromMouse(int my) {
        int by = getAbsoluteY() + borderWidth;
        int scrollVh = getScrollViewportHeight();
        if (scrollVh <= 0) return;
        float ratio = (float) (my - by - padding) / scrollVh;
        targetScrollOffset = (int) Math.max(0, Math.min(getMaxScroll(), ratio * getContentHeight() - scrollVh / 2f));
    }

    // ======== 悬停 ========

    private void updateHover(int mx, int my) {
        hoveredIndex = -1;
        scrollbarHovered = false;
        int absX = getAbsoluteX();
        int absY = getAbsoluteY();
        if (mx < absX || mx >= absX + width || my < absY || my >= absY + height) return;
        int x = absX + borderWidth;
        int y = absY + borderWidth;
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
        int sbX = x + vw - scrollbarWidth;
        scrollbarHovered = (mx >= sbX && mx < sbX + scrollbarWidth);
    }

    // ======== 渲染 ========

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = getAbsoluteX() + borderWidth;
        int y = getAbsoluteY() + borderWidth;
        int vw = getViewportWidth();
        int vh = getViewportHeight();

        // 1) 背景 + 边框
        if (borderWidth > 0 && borderColor != 0) {
            graphics.fill(getAbsoluteX(), getAbsoluteY(), getAbsoluteX() + width, getAbsoluteY() + height, applyAlpha(borderColor));
            graphics.fill(x, y, x + vw, y + vh, applyAlpha(bgColor));
        } else {
            graphics.fill(x, y, x + vw, y + vh, applyAlpha(bgColor));
        }

        if (slotItems.isEmpty()) return;

        // 平滑滚动：每帧向目标值插值逼近（滚轮/滚动条只改目标值）
        if (scrollOffset != targetScrollOffset) {
            int diff = targetScrollOffset - scrollOffset;
            int step = Math.max(1, (int) Math.ceil(Math.abs(diff) * 0.25));
            scrollOffset += Integer.signum(diff) * Math.min(step, Math.abs(diff));
            if (Math.abs(targetScrollOffset - scrollOffset) <= 1) {
                scrollOffset = targetScrollOffset;
            }
        }

        updateHover(mouseX, mouseY);
        int al = applyAlpha(0xFFFFFFFF);
        int a = (al & 0xFF000000);

        // 2) 可见卡片（逐行渲染，行内逐列）——scissor 裁剪到视口，防止滚动时画出背景区域
        int cardW = getCardWidth();
        int cardH = getCardHeight();
        int rowH = cardH + cellExtraHeight + vGap;
        int startRow = Math.max(0, scrollOffset / rowH - 1);
        int endRow = Math.min(getRowCount() - 1, (scrollOffset + vh) / rowH + 1);

        // 裁剪到内边距以内的区域：滚动时内容始终在 padding 带下方，上下边距恒定
        graphics.enableScissor(x + padding, y + padding, x + vw - padding, y + vh - padding);
        for (int row = startRow; row <= endRow; row++) {
            int cardTop = y + padding + row * rowH - scrollOffset;
            if (cardTop + cardH <= y || cardTop >= y + vh) continue;
            for (int col = 0; col < columns; col++) {
                int index = row * columns + col;
                if (index >= slotItems.size()) break;
                int cardX = x + padding + col * (cardW + hGap);
                boolean hover = index == hoveredIndex;
                if (hover) {
                    graphics.fill(cardX, cardTop, cardX + cardW, cardTop + cardH, applyAlpha(hoverColor));
                }
                slotItems.get(index).render(graphics, cardX, cardTop, cardW, hover, al);
            }
        }
        graphics.disableScissor();

        // 3) 滚动条（在 scissor 裁剪之外绘制；判断与 getCardWidth 的预留一致，
        //    避免"预留了空间却没画滚动条"造成右侧空白）
        int scrollVh = getScrollViewportHeight();
        if (needScrollInternal()) {
            int sbX = x + vw - scrollbarWidth;
            float ratio = (float) scrollVh / Math.max(scrollVh, getContentHeight());
            int sbH = Math.min(scrollVh, Math.max(8, (int) (scrollVh * ratio)));
            int trackH = Math.max(0, scrollVh - sbH);
            int sbY = y + padding + (getMaxScroll() > 0 ? (int) ((float) scrollOffset / getMaxScroll() * trackH) : 0);
            int sc = scrollbarHovered ? scrollbarHoverColor : scrollbarColor;
            graphics.fill(sbX, sbY, sbX + scrollbarWidth, sbY + sbH, a | (sc & 0x00FFFFFF));
        }
    }
}

package com.mohistmc.mod.api.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 统一滚动容器基类 — 垂直/水平滚动共用一套机制，新增滚动区域无需再调试滚动行为：
 * <ul>
 *   <li>滚轮滚动（每格 20px）</li>
 *   <li>滚动条点击跳转（thumb 中心跟随指针）</li>
 *   <li>滚动条长按拖拽（EnhancedScreen 自动转发）</li>
 *   <li>平滑滚动插值（每帧向目标值逼近，滚轮/滚动条只改目标）</li>
 *   <li>scissor 裁剪（半截内容平滑进出视口，无跳变间距）</li>
 *   <li>内容内边距（沿滚动方向两侧固定留白）</li>
 * </ul>
 * 子类只需实现内容布局：{@link #getContentSize()}、{@link #renderItems}、
 * {@link #handleContentClick}，可选 {@link #updateItemHover} / {@link #onDragContent}。
 * 现有子类：{@link ScrollList}（单列）、{@link GridScrollList}（网格）。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
@OnlyIn(Dist.CLIENT)
public abstract class ScrollableWidget extends PositionedWidget {

    /** 滚动方向 */
    public enum Direction { VERTICAL, HORIZONTAL }

    protected int bgColor = 0xFF2D2D2D;
    private Direction direction = Direction.VERTICAL;
    private int contentPadding; // 沿滚动方向两侧的内边距
    private int scrollStep = 20; // 滚轮每格滚动像素（网格/图标类内容可设为其步进对齐）
    private int hoverColor = 0x33FFFFFF;
    private int scrollbarWidth = 3;
    /** 轨道色（淡黄）：thumb 的可达范围 */
    private int scrollbarTrackColor = 0x33FFFF00;
    /** 定位色（绿） */
    private int scrollbarColor = 0xCC4CAF50;
    private int scrollbarHoverColor = 0xFF66BB6A;
    private int borderColor;
    private int borderWidth;

    protected int scrollOffset;
    protected int targetScrollOffset;
    protected boolean draggingScrollbar;
    protected boolean scrollbarHovered;
    protected int hoveredIndex = -1;
    protected int dragItemIndex = -1; // 正在拖拽的子项索引（-1=无）

    public ScrollableWidget(int relX, int relY, int width, int height) {
        super(relX, relY, Math.max(40, width), Math.max(20, height));
    }

    // ======== 链式配置 ========

    public ScrollableWidget setDirection(Direction d) { this.direction = d; return this; }
    public Direction getDirection() { return direction; }
    /** 内容内边距：滚动时首末条目与视口边缘保持固定间距（默认 0 不启用） */
    public ScrollableWidget setContentPadding(int padding) { this.contentPadding = Math.max(0, padding); return this; }
    /** 滚轮每格滚动像素（默认 20；网格/图标类内容可设为其步进值保持对齐） */
    public ScrollableWidget setScrollStep(int step) { this.scrollStep = Math.max(1, step); return this; }
    public ScrollableWidget setHoverColor(int c) { this.hoverColor = c; return this; }
    public ScrollableWidget setScrollbarColor(int c) { this.scrollbarColor = c; return this; }
    public ScrollableWidget setScrollbarHoverColor(int c) { this.scrollbarHoverColor = c; return this; }
    /** 轨道色（thumb 可达范围，淡色便于与定位色区分） */
    public ScrollableWidget setScrollbarTrackColor(int c) { this.scrollbarTrackColor = c; return this; }
    public ScrollableWidget setBorder(int color, int width) { this.borderColor = color; this.borderWidth = Math.max(0, width); return this; }

    @Override public ScrollableWidget setFlexGrow(int grow) { super.setFlexGrow(grow); return this; }
    @Override public ScrollableWidget setRightAnchored(boolean a) { super.setRightAnchored(a); return this; }
    @Override public ScrollableWidget setBottomAnchored(boolean a) { super.setBottomAnchored(a); return this; }
    @Override public ScrollableWidget setAlpha(int alpha) { super.setAlpha(alpha); return this; }
    @Override public ScrollableWidget setAlpha(float alpha) { super.setAlpha(alpha); return this; }

    // ======== 子类契约 ========

    /** 沿滚动方向的内容总尺寸（垂直 = 总高，水平 = 总宽） */
    public abstract int getContentSize();

    /** 渲染可见内容（已在 scissor 内，滚动偏移由子类应用；x/y 为内容区起点含边框） */
    protected abstract void renderItems(GuiGraphicsExtractor g, int x, int y, int vw, int vh, int alpha);

    /** 滚动条之外的点击（返回是否消费） */
    protected abstract boolean handleContentClick(MouseButtonEvent event, int bx, int by, int vw, int vh);

    /** 更新条目悬停（滚动条悬停由基类处理），默认无 */
    protected void updateItemHover(int mx, int my) {}

    /** 内容拖拽（如滑块子项），默认无 */
    protected void onDragContent(int mx, int my) {}

    // ======== 视口 / 滚动 ========

    public int getViewportWidth() { return width - borderWidth * 2; }
    public int getViewportHeight() { return height - borderWidth * 2; }
    public int getScrollOffset() { return scrollOffset; }
    public int getHoveredIndex() { return hoveredIndex; }
    protected int getContentPadding() { return contentPadding; }
    protected int getBorderWidth() { return borderWidth; }
    protected int getScrollbarWidth() { return scrollbarWidth; }

    /** 沿滚动方向的视口尺寸（扣内边距） */
    protected int getScrollViewportSize() {
        int vp = direction == Direction.VERTICAL ? getViewportHeight() : getViewportWidth();
        return Math.max(0, vp - contentPadding * 2);
    }

    /** 是否需要滚动条 */
    protected boolean needScroll() { return getContentSize() > getScrollViewportSize(); }

    /** 最大滚动偏移（内容加上下内边距后超出视口的量） */
    public int getMaxScroll() {
        int vp = direction == Direction.VERTICAL ? getViewportHeight() : getViewportWidth();
        return Math.max(0, getContentSize() + contentPadding * 2 - vp);
    }

    /** 沿滚动方向的指针坐标（垂直 = y，水平 = x） */
    protected int pointerAxis(double mx, double my) {
        return direction == Direction.VERTICAL ? (int) my : (int) mx;
    }

    /** 沿滚动方向的内容区起点（垂直 = y，水平 = x） */
    protected int axisStart(int x, int y) {
        return direction == Direction.VERTICAL ? y : x;
    }

    // ======== 滚轮 ========

    boolean handleScroll(double mouseX, double mouseY, double delta) {
        int ax = getAbsoluteX();
        int ay = getAbsoluteY();
        if (mouseX < ax || mouseX >= ax + width || mouseY < ay || mouseY >= ay + height) return false;
        int old = targetScrollOffset;
        targetScrollOffset = (int) Math.max(0, Math.min(getMaxScroll(), targetScrollOffset - delta * scrollStep));
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

        // 滚动条：按下开始长按拖动（thumb 中心跟随指针）
        if (needScroll() && onScrollbar(mx, my, bx, by, vw, vh)) {
            draggingScrollbar = true;
            updateScrollbarFromMouse(pointerAxis(mx, my) - axisStart(bx, by));
            return true;
        }

        return handleContentClick(event, bx, by, vw, vh);
    }

    /** 指针是否落在滚动条上（垂直 = 右侧竖条，水平 = 底部横条） */
    protected boolean onScrollbar(int mx, int my, int bx, int by, int vw, int vh) {
        if (direction == Direction.VERTICAL) {
            int sbX = bx + vw - scrollbarWidth;
            return mx >= sbX && mx < sbX + scrollbarWidth;
        }
        int sbY = by + vh - scrollbarWidth;
        return my >= sbY && my < sbY + scrollbarWidth;
    }

    // ======== 拖拽 / 释放 ========

    void handleDrag(int mx, int my) {
        if (draggingScrollbar) {
            updateScrollbarFromMouse(pointerAxis(mx, my) - axisStart(getAbsoluteX() + borderWidth, getAbsoluteY() + borderWidth));
            return;
        }
        onDragContent(mx, my);
    }

    void handleRelease() {
        draggingScrollbar = false;
        dragItemIndex = -1;
    }

    /** 按指针位置更新滚动目标（thumb 中心跟随） */
    private void updateScrollbarFromMouse(int relPos) {
        int vp = getScrollViewportSize();
        if (vp <= 0) return;
        float ratio = (float) relPos / vp;
        targetScrollOffset = (int) Math.max(0, Math.min(getMaxScroll(), ratio * getContentSize() - vp / 2f));
    }

    // ======== 渲染 ========

    @Override
    public void render(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        int x = getAbsoluteX() + borderWidth;
        int y = getAbsoluteY() + borderWidth;
        int vw = getViewportWidth();
        int vh = getViewportHeight();

        // 1) 背景 + 边框
        if (borderWidth > 0 && borderColor != 0) {
            g.fill(getAbsoluteX(), getAbsoluteY(), getAbsoluteX() + width, getAbsoluteY() + height, applyAlpha(borderColor));
            g.fill(x, y, x + vw, y + vh, applyAlpha(bgColor));
        } else {
            g.fill(x, y, x + vw, y + vh, applyAlpha(bgColor));
        }

        if (getContentSize() <= 0) return;

        // 2) 钳制滚动偏移（内容变化后 maxScroll 可能变小，防止滑出内容区/布局）
        int maxScroll = getMaxScroll();
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;
        if (targetScrollOffset > maxScroll) targetScrollOffset = maxScroll;

        // 3) 平滑滚动：每帧向目标值插值逼近（滚轮/滚动条只改目标值）
        if (scrollOffset != targetScrollOffset) {
            int diff = targetScrollOffset - scrollOffset;
            int step = Math.max(1, (int) Math.ceil(Math.abs(diff) * 0.25));
            scrollOffset += Integer.signum(diff) * Math.min(step, Math.abs(diff));
            if (Math.abs(targetScrollOffset - scrollOffset) <= 1) {
                scrollOffset = targetScrollOffset;
            }
        }

        // 3) 悬停
        scrollbarHovered = false;
        updateItemHover(mouseX, mouseY);
        if (isMouseOver(mouseX, mouseY)) {
            scrollbarHovered = onScrollbar(mouseX, mouseY, x, y, vw, vh);
        }

        // 4) 内容（scissor 裁剪到内边距带，半截条目平滑进出视口；
        //    水平方向左右各扩 1px，容纳内容自绘的 1px 描边边框，避免边线被裁）
        int pad = contentPadding;
        int al = applyAlpha(0xFFFFFFFF);
        if (direction == Direction.VERTICAL) {
            g.enableScissor(x, y + pad, x + vw, y + vh - pad);
        } else {
            g.enableScissor(x + pad - 1, y, x + vw - pad + 1, y + vh);
        }
        renderItems(g, x, y, vw, vh, al);
        g.disableScissor();

        // 5) 滚动条（scissor 之外绘制）
        if (needScroll()) {
            renderScrollbar(g, x, y, vw, vh, al);
        }
    }

    private void renderScrollbar(GuiGraphicsExtractor g, int x, int y, int vw, int vh, int al) {
        int sc = scrollbarHovered || draggingScrollbar ? scrollbarHoverColor : scrollbarColor;
        int pad = contentPadding;
        if (direction == Direction.VERTICAL) {
            int sbX = x + vw - scrollbarWidth;
            // 轨道（淡黄）：thumb 的可达范围
            g.fill(sbX, y + pad, sbX + scrollbarWidth, y + vh - pad, applyAlpha(scrollbarTrackColor));
            float ratio = (float) vh / Math.max(vh, getContentSize());
            int sbH = Math.min(vh - pad * 2, Math.max(8, (int) (vh * ratio)));
            // 轨道内可用滑动距离（轨道高 - thumb 高），滚到底 thumb 底精确对齐轨道底
            int trackH = Math.max(0, vh - pad * 2 - sbH);
            int sbY = y + pad + (getMaxScroll() > 0 ? (int) ((float) scrollOffset / getMaxScroll() * trackH) : 0);
            // 防御钳制：任何比例下 thumb 都不越出轨道
            sbY = Math.max(y + pad, Math.min(y + vh - pad - sbH, sbY));
            g.fill(sbX, sbY, sbX + scrollbarWidth, sbY + sbH, applyAlpha(sc));
        } else {
            int sbY = y + vh - scrollbarWidth;
            // 轨道（淡黄）
            g.fill(x + pad, sbY, x + vw - pad, sbY + scrollbarWidth, applyAlpha(scrollbarTrackColor));
            float ratio = (float) vw / Math.max(vw, getContentSize());
            int sbW = Math.min(vw - pad * 2, Math.max(8, (int) (vw * ratio)));
            // 轨道内可用滑动距离
            int trackW = Math.max(0, vw - pad * 2 - sbW);
            int sbX = x + pad + (getMaxScroll() > 0 ? (int) ((float) scrollOffset / getMaxScroll() * trackW) : 0);
            // 防御钳制：任何比例下 thumb 都不越出轨道
            sbX = Math.max(x + pad, Math.min(x + vw - pad - sbW, sbX));
            g.fill(sbX, sbY, sbX + sbW, sbY + scrollbarWidth, applyAlpha(sc));
        }
    }

    /** 悬停填充（子类渲染条目时调用） */
    protected void fillHover(GuiGraphicsExtractor g, int x, int y, int w, int h, boolean hover) {
        if (hover) {
            g.fill(x, y, x + w, y + h, applyAlpha(hoverColor));
        }
    }
}

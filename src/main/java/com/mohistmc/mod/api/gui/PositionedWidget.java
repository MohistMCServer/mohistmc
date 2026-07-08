package com.mohistmc.mod.api.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class PositionedWidget {
    protected int relativeX, relativeY;
    public int width;
    public int height;
    protected int screenLeft, screenTop; // 由 Screen 注入
    protected int contentWidth, contentHeight; // 内容区域大小（用于锚点计算）
    protected boolean anchorRight, anchorBottom; // 是否锚定右下
    protected int alpha = 255; // 自身透明度 0-255，默认不透明
    protected int flexGrow = 0; // 弹性增长因子，0=固定大小，>0 在布局中按比例占据剩余空间

    public PositionedWidget(int relX, int relY, int width, int height) {
        this.relativeX = relX;
        this.relativeY = relY;
        this.width = width;
        this.height = height;
    }

    // ======== 锚点配置（链式） ========

    /** 右锚定：relX 改为距右边缘的距离 */
    public PositionedWidget setRightAnchored(boolean anchored) {
        this.anchorRight = anchored;
        return this;
    }

    /** 下锚定：relY 改为距下边缘的距离 */
    public PositionedWidget setBottomAnchored(boolean anchored) {
        this.anchorBottom = anchored;
        return this;
    }

    // ======== 透明度（链式） ========

    /** 设置自身透明度 (0-255)，0=完全透明，255=不透明 */
    public PositionedWidget setAlpha(int alpha) {
        this.alpha = alpha & 0xFF;
        return this;
    }

    /** 设置自身透明度 (0.0 ~ 1.0) */
    public PositionedWidget setAlpha(float alpha) {
        this.alpha = (int) (alpha * 255) & 0xFF;
        return this;
    }

    /** 设置弹性增长因子（仅在有布局管理的父容器中生效） */
    public PositionedWidget setFlexGrow(int grow) {
        this.flexGrow = Math.max(0, grow);
        return this;
    }

    /** 获取当前透明度 */
    public int getAlpha() {
        return alpha;
    }

    /**
     * 将颜色乘以当前透明度返回新 ARGB 值
     * <p>如 alpha=128 且原 alpha=255 → 返回颜色的 alpha 变为 128</p>
     */
    public int applyAlpha(int argb) {
        if (alpha == 255) return argb;
        int a = (argb >>> 24) * alpha / 255;
        return (a << 24) | (argb & 0xFFFFFF);
    }

    // ======== 内部 ========

    void setScreenPos(int left, int top, int contentWidth, int contentHeight) {
        this.screenLeft = left;
        this.screenTop = top;
        this.contentWidth = contentWidth;
        this.contentHeight = contentHeight;
        updateAbsolutePosition();
    }

    protected void updateAbsolutePosition() {
        // 可被子类重写
    }

    public int getAbsoluteX() {
        if (anchorRight) {
            return screenLeft + contentWidth - relativeX - width;
        }
        return screenLeft + relativeX;
    }

    public int getAbsoluteY() {
        if (anchorBottom) {
            return screenTop + contentHeight - relativeY - height;
        }
        return screenTop + relativeY;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        int x = getAbsoluteX();
        int y = getAbsoluteY();
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public abstract void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick);
}

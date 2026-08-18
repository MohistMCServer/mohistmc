package com.mohistmc.mod.api.gui;

/**
 * 坐标转换工具 — 在屏幕坐标和逻辑（基准分辨率）坐标之间转换。
 * EnhancedScreen 每帧设置转换参数，所有组件通过此类将事件坐标转换为逻辑坐标。
 */
public final class GuiCoord {
    private static float scale = 1.0f;
    private static int offsetX = 0;
    private static int offsetY = 0;

    private GuiCoord() {}

    /** 由 EnhancedScreen 每帧调用以更新转换参数 */
    public static void setTransform(float s, int ox, int oy) {
        scale = s;
        offsetX = ox;
        offsetY = oy;
    }

    /** 重置为 1:1 映射 */
    public static void reset() {
        scale = 1.0f;
        offsetX = 0;
        offsetY = 0;
    }

    /** 缩放因子 */
    public static float scale() { return scale; }

    /** 屏幕坐标 → 逻辑 X */
    public static int toLogicalX(double screenX) {
        return (int) ((screenX - offsetX) / scale);
    }

    /** 屏幕坐标 → 逻辑 Y */
    public static int toLogicalY(double screenY) {
        return (int) ((screenY - offsetY) / scale);
    }

    /** 屏幕坐标 → 逻辑 X（浮点精度） */
    public static float toLogicalXF(double screenX) {
        return (float) ((screenX - offsetX) / scale);
    }

    /** 屏幕坐标 → 逻辑 Y（浮点精度） */
    public static float toLogicalYF(double screenY) {
        return (float) ((screenY - offsetY) / scale);
    }

    /** 逻辑坐标 → 屏幕 X */
    public static int toScreenX(double logicalX) {
        return (int) (offsetX + logicalX * scale);
    }

    /** 逻辑坐标 → 屏幕 Y */
    public static int toScreenY(double logicalY) {
        return (int) (offsetY + logicalY * scale);
    }
}

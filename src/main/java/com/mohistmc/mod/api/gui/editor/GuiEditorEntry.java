package com.mohistmc.mod.api.gui.editor;

/**
 * 单个组件的布局配置项 — 记录坐标、尺寸、锚点信息
 */
public class GuiEditorEntry {
    public String id;
    public String type;
    public int x, y, w, h;
    public boolean anchorRight;
    public boolean anchorBottom;

    public GuiEditorEntry() {}

    public GuiEditorEntry(String id, String type, int x, int y, int w, int h,
                          boolean anchorRight, boolean anchorBottom) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.anchorRight = anchorRight;
        this.anchorBottom = anchorBottom;
    }
}
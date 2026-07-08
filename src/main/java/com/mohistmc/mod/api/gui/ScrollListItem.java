package com.mohistmc.mod.api.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 滚动列表中的自定义子项基类 — 每个子项独立控制渲染和交互。
 * 子类：{@link LabelItem}、{@link CheckboxItem}、{@link ToggleItem}、{@link SliderItem}
 */
@OnlyIn(Dist.CLIENT)
public abstract class ScrollListItem {
    protected int height = 20;
    public int getHeight() { return height; }
    public ScrollListItem setHeight(int h) { height = Math.max(8, h); return this; }

    /**
     * 处理点击
     * @param rx 点击位置相对于当前项左上角的 X
     * @param ry 点击位置相对于当前项左上角的 Y
     * @param w  项渲染宽度
     * @return true 表示消费点击
     */
    public boolean handleClick(int rx, int ry, int w) { return false; }

    /** 渲染当前项 */
    public abstract void render(GuiGraphicsExtractor g, int x, int y, int w, boolean hovered, int alpha);

    // ======== 工具 ========
    protected static Minecraft mc() { return Minecraft.getInstance(); }
    protected static net.minecraft.client.gui.Font font() { return mc().font; }
}

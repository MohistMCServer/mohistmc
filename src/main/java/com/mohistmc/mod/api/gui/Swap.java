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
 * 交换/标签容器 — 通过标签栏切换显示不同的子页面，同时只显示一个活动页。
 *
 * <pre>
 *   var swap = new Swap(x, y, w, h, 0xFF2D2D2D);
 *   swap.addPage(editPanel,   Component.literal("编辑"));
 *   swap.addPage(previewPanel, Component.literal("预览"));
 *   swap.setActiveIndex(0);
 * </pre>
 */
public class Swap extends Panel {

    private final List<PositionedWidget> pages = new ArrayList<>();
    private final List<Component> pageLabels = new ArrayList<>();
    private int activeIndex;

    // 标签栏样式
    private int tabHeight = 18;
    private int activeTabColor = 0xFF4CAF50;
    private int inactiveTabColor = 0xFF3A3A3A;
    private int activeTextColor = 0xFFFFFFFF;
    private int inactiveTextColor = 0xFFAAAAAA;
    private int tabBorderColor = 0xFF555555;
    private int indicatorColor = 0xFF66BB6A;

    private int hoveredTab = -1;
    private Consumer<Integer> onSwap;

    // ======== 构造 ========

    public Swap(int relX, int relY, int width, int height, int bgColor) {
        super(relX, relY, width, height, bgColor);
    }

    // ======== 链式配置 ========

    /** 添加一页（子组件 + 标签文字） */
    public Swap addPage(PositionedWidget content, Component label) {
        pages.add(content);
        pageLabels.add(label);
        super.addChild(content); // 加入 Panel 的子组件列表，供点击递归查找
        if (pages.size() == 1) activeIndex = 0;
        return this;
    }

    /** 设置当前显示页 */
    public Swap setActiveIndex(int index) {
        activeIndex = index >= 0 && index < pages.size() ? index : 0;
        return this;
    }

    public int getActiveIndex() { return activeIndex; }

    public Swap setTabHeight(int h) { tabHeight = Math.max(12, h); return this; }
    public Swap setActiveTabColor(int c) { activeTabColor = c; return this; }
    public Swap setInactiveTabColor(int c) { inactiveTabColor = c; return this; }
    public Swap setOnSwap(Consumer<Integer> cb) { onSwap = cb; return this; }

    // ======== 点击 ========

    @Override
    boolean handleClick(MouseButtonEvent event, boolean doubleClick) {
        int mx = logicalX(event);
        int my = logicalY(event);
        int x = getAbsoluteX();
        int y = getAbsoluteY();

        // 只在点击标签栏时处理
        if (my < y || my >= y + tabHeight || mx < x || mx >= x + width) return false;

        int n = pageLabels.size();
        if (n == 0) return false;
        int tabW = width / n;
        int idx = (mx - x) / tabW;
        if (idx >= 0 && idx < n && idx != activeIndex) {
            activeIndex = idx;
            if (onSwap != null) onSwap.accept(idx);
        }
        return true; // 标签栏上的点击总是被消费
    }

    // ======== 渲染 ========

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = getAbsoluteX();
        int y = getAbsoluteY();
        int tabH = tabHeight;
        int n = pageLabels.size();
        var font = Minecraft.getInstance().font;

        // 1) 标签栏背景
        graphics.fill(x, y, x + width, y + tabH, applyAlpha(inactiveTabColor));

        if (n > 0) {
            int tabW = width / n;
            hoveredTab = -1;

            for (int i = 0; i < n; i++) {
                int tx = x + i * tabW;
                boolean active = i == activeIndex;
                boolean hover = mouseX >= tx && mouseX < tx + tabW
                        && mouseY >= y && mouseY < y + tabH;
                if (hover) hoveredTab = i;

                // 标签背景
                int bg;
                if (active) {
                    bg = applyAlpha(activeTabColor);
                } else if (hover) {
                    bg = brighten(inactiveTabColor, 0.25f);
                } else {
                    bg = applyAlpha(inactiveTabColor);
                }
                graphics.fill(tx, y, tx + tabW, y + tabH, bg);

                // 活跃标签指示条（底部）
                if (active) {
                    graphics.fill(tx, y + tabH - 2, tx + tabW, y + tabH, applyAlpha(indicatorColor));
                }

                // 文字
                int tc = active ? applyAlpha(activeTextColor) : applyAlpha(inactiveTextColor);
                int textX = tx + (tabW - font.width(pageLabels.get(i))) / 2;
                int textY = y + (tabH - font.lineHeight) / 2;
                graphics.text(font, pageLabels.get(i), textX, textY, tc);
            }

            // 标签栏底部分隔线
            graphics.fill(x, y + tabH - 1, x + width, y + tabH, applyAlpha(tabBorderColor));
        }

        // 2) 内容区背景
        int cy = y + tabH;
        int ch = height - tabH;
        if (ch > 0) {
            graphics.fill(x, cy, x + width, y + height, applyAlpha(backgroundColor));
        }

        // 3) 只渲染当前活跃页（覆盖宽高让其自适应容器）
        if (activeIndex >= 0 && activeIndex < pages.size()) {
            var page = pages.get(activeIndex);
            page.width = width;
            page.height = ch;
            page.setScreenPos(x, cy, width, ch);
            page.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    // ======== 工具 ========

    private static int brighten(int color, float factor) {
        int a = color >>> 24;
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) * (1 + factor)));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) * (1 + factor)));
        int b = Math.min(255, (int) ((color & 0xFF) * (1 + factor)));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}

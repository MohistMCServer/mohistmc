package com.mohistmc.mod.api.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * 徽章组件 — 深色圆角条 + 可选前置图标 + 文本（商品价格、标签等场景）
 *
 * <pre>
 *   // 作为独立组件加入 Screen
 *   addWidget(new Badge(x, y, w, h).setIcon(tex, 8).setText(Component.literal("80")));
 *
 *   // 在 ScrollListItem 内便捷静态渲染（不参与组件树）
 *   Badge.render(g, x, y, w, h, iconTex, 4, priceText, 0xCC222222, 0xFFFFFFFF, alpha);
 * </pre>
 */
public class Badge extends PositionedWidget {

    @Nullable private Identifier iconTexture;
    private int iconSize = 8;
    private int iconSrcSize = 32;
    private Component text = Component.literal("");
    private int bgColor = 0xCC222222;
    private int textColor = 0xFFFFFFFF;
    private int borderRadius = 3;
    private int borderColor;
    private int borderWidth;

    public Badge(int relX, int relY, int width, int height) {
        super(relX, relY, width, height);
    }

    /** @param displaySize 显示尺寸；@param srcSize 贴图源边长（完整贴图缩放显示） */
    public Badge setIcon(@Nullable Identifier texture, int displaySize, int srcSize) {
        this.iconTexture = texture;
        this.iconSize = Math.max(1, displaySize);
        this.iconSrcSize = Math.max(1, srcSize);
        return this;
    }

    public Badge setText(Component text) {
        this.text = text;
        return this;
    }

    public Badge setBgColor(int color) {
        this.bgColor = color;
        return this;
    }

    public Badge setTextColor(int color) {
        this.textColor = color;
        return this;
    }

    public Badge setBorderRadius(int radius) {
        this.borderRadius = Math.max(0, radius);
        return this;
    }

    /** 设置边框（颜色 + 宽度，0 为无边框） */
    public Badge setBorder(int color, int width) {
        this.borderColor = color;
        this.borderWidth = Math.max(0, width);
        return this;
    }

    @Override
    public void render(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        render(g, getAbsoluteX(), getAbsoluteY(), width, height,
                iconTexture, iconSize, iconSrcSize, text, bgColor, textColor,
                applyAlpha(0xFFFFFFFF), borderRadius, borderColor, borderWidth);
    }

    /** 便捷静态渲染（供 ScrollListItem 等无法挂组件树的位置使用） */
    public static void render(GuiGraphicsExtractor g, int x, int y, int w, int h,
                              @Nullable Identifier iconTexture, int iconSize, int iconSrcSize,
                              Component text, int bgColor, int textColor, int alpha, int borderRadius,
                              int borderColor, int borderWidth) {
        int a = alpha & 0xFF000000; // 取 alpha 通道
        if (borderWidth > 0) {
            // 边框：先画边框色整块，再内缩画背景
            g.fill(x, y, x + w, y + h, a | (borderColor & 0x00FFFFFF));
            int ix = x + borderWidth;
            int iy = y + borderWidth;
            int iw = w - borderWidth * 2;
            int ih = h - borderWidth * 2;
            if (borderRadius > 0) {
                CustomButton.fillRoundRect(g, ix, iy, iw, ih, borderRadius, a | (bgColor & 0x00FFFFFF));
            } else {
                g.fill(ix, iy, ix + iw, iy + ih, a | (bgColor & 0x00FFFFFF));
            }
        } else if (borderRadius > 0) {
            CustomButton.fillRoundRect(g, x, y, w, h, borderRadius, a | (bgColor & 0x00FFFFFF));
        } else {
            g.fill(x, y, x + w, y + h, a | (bgColor & 0x00FFFFFF));
        }

        var font = net.minecraft.client.Minecraft.getInstance().font;
        int textW = font.width(text);
        int iconW = iconTexture != null ? iconSize + 2 : 0;
        int totalW = textW + iconW;
        int startX = x + (w - totalW) / 2;
        int textY = y + (h - font.lineHeight) / 2;

        if (iconTexture != null) {
            // 完整图标贴图缩放为 iconSize（pose 缩放：平移到目标原点 → 缩放 → 回退），
            // 按徽章高度垂直居中并向下取整（数字视觉中心偏下，与之对齐）
            int iconY = y + (h - iconSize + 1) / 2;
            var pose = g.pose();
            pose.pushMatrix();
            pose.translate(startX, iconY);
            pose.scale(iconSize / (float) iconSrcSize, iconSize / (float) iconSrcSize);
            pose.translate(-startX, -iconY);
            g.blit(RenderPipelines.GUI_TEXTURED, iconTexture, startX, iconY, 0, 0, iconSrcSize, iconSrcSize, iconSrcSize, iconSrcSize);
            pose.popMatrix();
            startX += iconW;
        }
        g.text(font, text, startX, textY, a | (textColor & 0x00FFFFFF));
    }
}

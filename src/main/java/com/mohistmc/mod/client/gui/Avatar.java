package com.mohistmc.mod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 头像组件 — 圆形/圆角缩略图，支持纹理和首字母回退。
 *
 * <pre>
 *   // 纹理头像
 *   new Avatar(x, y, 32).setTexture(Identifier.of("mohistmc", "textures/gui/avatar.png"));
 *
 *   // 首字母回退
 *   new Avatar(x, y, 32, "M").setBackground(0xFF4CAF50);
 *
 *   // 纯色圆点
 *   new Avatar(x, y, 16).setBackground(0xFF9d2933).setShape(Shape.CIRCLE);
 * </pre>
 */
@OnlyIn(Dist.CLIENT)
public class Avatar extends PositionedWidget {

    /** 头像形状 */
    public enum Shape { CIRCLE, ROUNDED, SQUARE }

    // ======== 数据 ========
    private Identifier texture;
    private Identifier borderTexture; // 边框纹理（叠加在头像之上）

    // ======== 样式 ========
    private String initials;
    private int bgColor = 0xFF555555;
    private int textColor = 0xFFFFFFFF;
    private int borderColor;
    private int borderWidth;
    private Shape shape = Shape.CIRCLE;
    private int roundRadius = 4;

    // ======== 构造 ========

    /** 仅背景色（可作为小圆点使用） */
    public Avatar(int relX, int relY, int size) {
        super(relX, relY, Math.max(8, size), Math.max(8, size));
    }

    /** 首字母头像 */
    public Avatar(int relX, int relY, int size, String initials) {
        this(relX, relY, size);
        this.initials = initials != null && !initials.isEmpty() ? initials.substring(0, 1).toUpperCase() : null;
    }

    // ======== 链式配置 ========

    /** 设置纹理 */
    public Avatar setTexture(Identifier tex) {
        this.texture = tex;
        return this;
    }

    /** 设置首字母（覆盖 texture） */
    public Avatar setInitials(String s) {
        this.initials = s != null && !s.isEmpty() ? s.substring(0, 1).toUpperCase() : null;
        return this;
    }

    public Avatar setBackground(int c) { this.bgColor = c; return this; }
    public Avatar setTextColor(int c) { this.textColor = c; return this; }

    /** 设置边框纹理（叠加在头像之上，如装饰框 / 稀有度外框） */
    public Avatar setBorderTexture(Identifier tex) {
        this.borderTexture = tex;
        return this;
    }

    /** 边框 */
    public Avatar setBorder(int color, int width) {
        this.borderColor = color;
        this.borderWidth = Math.max(0, width);
        return this;
    }

    /** 形状 */
    public Avatar setShape(Shape s) { this.shape = s; return this; }
    public Avatar setRoundRadius(int r) { this.roundRadius = Math.max(1, r); return this; }
    public Avatar asCircle() { this.shape = Shape.CIRCLE; return this; }

    @Override public Avatar setRightAnchored(boolean a) { super.setRightAnchored(a); return this; }
    @Override public Avatar setBottomAnchored(boolean a) { super.setBottomAnchored(a); return this; }

    // ======== 渲染 ========

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = getAbsoluteX();
        int y = getAbsoluteY();
        int s = Math.min(width, height);

        if (texture != null) {
            // ─── 纹理模式：直接渲染，不做任何形状/边框裁剪 ───
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, s, s, s, s);
            if (borderTexture != null) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, borderTexture, x, y, 0, 0, s, s, s, s);
            }
            return;
        }

        // ─── 非纹理模式：背景 + 形状裁剪 + 首字母 ───
        int r;
        if (shape == Shape.CIRCLE)       r = s / 2;
        else if (shape == Shape.ROUNDED) r = Math.min(roundRadius, s / 2);
        else                             r = 0;

        // 1) 边框 + 背景色基底
        if (borderWidth > 0 && borderColor != 0) {
            int bc = applyAlpha(borderColor);
            fillClip(graphics, x, y, s, s, r, bc);
            int ins = borderWidth;
            int ir = Math.max(0, r - borderWidth);
            fillClip(graphics, x + ins, y + ins, s - ins * 2, s - ins * 2, ir, applyAlpha(bgColor));
        } else {
            fillClip(graphics, x, y, s, s, r, applyAlpha(bgColor));
        }

        // 2) 首字母
        if (initials != null) {
            var font = Minecraft.getInstance().font;
            int tx = x + (s - font.width(initials)) / 2;
            int ty = y + (s - font.lineHeight) / 2;
            graphics.text(font, initials, tx, ty, applyAlpha(textColor));
        }

        // 3) 边框纹理
        if (borderTexture != null) {
            drawClipped(graphics, x, y, s, s, r, borderTexture);
        }
    }

    // ======== 圆角裁剪 ========

    private static void fillClip(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int color) {
        // 填充圆角矩形主体，四角留出透明缝隙（露出父背景形成圆角视觉效果）
        if (r <= 0) { if (w > 0 && h > 0) g.fill(x, y, x + w, y + h, color); return; }
        r = Math.min(r, Math.min(w / 2, h / 2));
        g.fill(x + r, y, x + w - r, y + h, color);
        g.fill(x, y + r, x + r, y + h - r, color);
        g.fill(x + w - r, y + r, x + w, y + h - r, color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int px = (int) Math.sqrt(r * r - dy * dy);
            px = Math.min(px, r);
            if (px > 0) {
                g.fill(x + r - px, y + i, x + r, y + i + 1, color);
                g.fill(x + w - r, y + i, x + w - r + px, y + i + 1, color);
                g.fill(x + r - px, y + h - i - 1, x + r, y + h - i, color);
                g.fill(x + w - r, y + h - i - 1, x + w - r + px, y + h - i, color);
            }
        }
    }

    /** 在圆角区域内绘制纹理（背景色覆盖四角外部，保留内部纹理可见） */
    private void drawClipped(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, Identifier tex) {
        g.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, 0, 0, w, h, w, h);
        if (r <= 0) return;
        r = Math.min(r, Math.min(w / 2, h / 2));
        int bg = 0xFF000000 | bgColor;
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int px = (int) Math.sqrt(r * r - dy * dy);
            px = Math.min(px, r);
            if (px > 0) {
                // 只覆盖圆弧外部（从方块边缘 → 圆弧边缘），内部让纹理可见
                g.fill(x, y + i, x + r - px, y + i + 1, bg);                     // TL 外部
                g.fill(x + w - r + px, y + i, x + w, y + i + 1, bg);             // TR 外部
                g.fill(x, y + h - i - 1, x + r - px, y + h - i, bg);             // BL 外部
                g.fill(x + w - r + px, y + h - i - 1, x + w, y + h - i, bg);     // BR 外部
            }
        }
    }
}

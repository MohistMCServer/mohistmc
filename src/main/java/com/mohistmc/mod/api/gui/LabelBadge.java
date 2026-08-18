package com.mohistmc.mod.api.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * 文字徽章组件 — 圆角矩形背景 + 居中文字，宽度随文字内容自动增长（左右对称）。
 *
 * <p>与 {@link Badge}（需指定宽高）不同，本组件自动计算尺寸：
 * 宽度 = 文字宽×缩放 + 左右内边距×2，高度 = 文字高×缩放 + 上下内边距×2。
 * 文字始终在徽章内居中，也可通过 {@link #setFixedWidth(int)} 手动固定宽度，
 * 编辑器调整尺寸后文字仍保持居中。典型用途：玩家名、标签等。</p>
 */
@OnlyIn(Dist.CLIENT)
public class LabelBadge extends PositionedWidget {

    /** 文字对齐方式（组件内） */
    public enum Align { LEFT, CENTER, RIGHT }

    private Component text;
    private float textScale = 1.0f;
    private Align align = Align.CENTER;
    private int textColor = 0xFFFFFFFF;
    private int bgColor = 0x66000000;      // 半透明深色底
    private int borderColor = 0xFFAAAAAA;
    private int borderWidth = 1;
    private int paddingX = 2;  // 徽章四方向内边距（2 格）
    private int paddingY = 2;
    private int borderRadius = 6;
    /** 手动固定宽度（-1 = 自动适应文字） */
    private int fixedWidth = -1;
    /** 前置图标（null = 无图标） */
    @Nullable private Identifier iconTexture;
    private int iconSize = 14;
    /** 贴图源边长（从资源读取，0 = 未知，按 1:1 显示） */
    private int iconSrcSize;
    private int iconGap = 4;
    /** 自动缩放基准高度（0 = 关闭；开启后根据父容器高度自动计算文字缩放） */
    private int autoScaleBase = 0;
    private static final float AUTO_SCALE_MIN = 0.7f;
    private static final float AUTO_SCALE_MAX = 2.5f;

    public LabelBadge(int relX, int relY, Component text) {
        super(relX, relY, 0, 0);
        this.text = text;
        autoSize();
    }

    // ======== 链式配置 ========

    public LabelBadge setTextScale(float s) {
        textScale = Math.max(0.1f, s);
        autoSize();
        return this;
    }

    public float getTextScale() { return textScale; }

    public LabelBadge setFontSize(int size) {
        textScale = Math.max(0.1f, size / 12f);
        autoSize();
        return this;
    }

    /** 设置文字在徽章内的对齐方式（默认居中） */
    public LabelBadge setAlign(Align a) {
        align = a != null ? a : Align.CENTER;
        return this;
    }

    public LabelBadge setTextColor(int color) {
        this.textColor = color;
        return this;
    }

    public LabelBadge setBgColor(int color) {
        this.bgColor = color;
        return this;
    }

    public LabelBadge setBorderColor(int color) {
        this.borderColor = color;
        return this;
    }

    public LabelBadge setBorderWidth(int w) {
        this.borderWidth = Math.max(0, w);
        return this;
    }

    public LabelBadge setPaddingX(int px) {
        this.paddingX = Math.max(0, px);
        autoSize();
        return this;
    }

    public LabelBadge setPaddingY(int py) {
        this.paddingY = Math.max(0, py);
        autoSize();
        return this;
    }

    public LabelBadge setBorderRadius(int r) {
        this.borderRadius = Math.max(0, r);
        return this;
    }

    /** 手动固定宽度（-1 恢复自动适应文字） */
    public LabelBadge setFixedWidth(int w) {
        this.fixedWidth = w;
        autoSize();
        return this;
    }

    /**
     * 设置前置图标（仅需纹理）。
     * 图标显示尺寸自动 = 徽章内容高度（保持四方向 2 格内边距），
     * 贴图从资源读取实际边长后自动缩放；读取失败时按 1:1 显示。
     */
    public LabelBadge setIcon(@Nullable Identifier texture) {
        this.iconTexture = texture;
        this.iconSrcSize = readTextureSize(texture);
        autoSize();
        return this;
    }

    /** 读取贴图实际边长（PNG 头解析），未知返回 0（按 1:1 显示） */
    private int readTextureSize(@Nullable Identifier id) {
        if (id == null) return 0;
        try {
            var opt = Minecraft.getInstance().getResourceManager().getResource(id);
            if (opt.isPresent()) {
                try (var in = opt.get().open()) {
                    byte[] hdr = in.readNBytes(24); // PNG: 8 签名 + 4 长度 + 4 "IHDR" + 4 宽 + 4 高
                    if (hdr.length >= 24 && hdr[0] == (byte) 0x89 && hdr[1] == 0x50
                            && hdr[2] == 0x4E && hdr[3] == 0x47) {
                        int w = ((hdr[16] & 0xFF) << 24) | ((hdr[17] & 0xFF) << 16)
                                | ((hdr[18] & 0xFF) << 8) | (hdr[19] & 0xFF);
                        return Math.max(1, w); // UI 图标视为方形
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return 0;
    }

    /**
     * 开启自动缩放：文字缩放根据父容器高度自动计算
     * （textScale = clamp(父容器高度 / baseHeight, 0.7, 2.5)）。
     * 父容器尺寸在加入 Panel/Screen 后生效。
     */
    public LabelBadge setAutoScale(int baseHeight) {
        this.autoScaleBase = Math.max(0, baseHeight);
        return this;
    }

    /** 当前内容自然宽度（图标 + 文字宽×缩放 + 左右内边距） */
    public int getContentWidth() {
        var f = Minecraft.getInstance().font;
        int textW = (int) (f.width(text) * textScale);
        int iconW = iconTexture != null ? iconSize + iconGap : 0;
        return iconW + textW + paddingX * 2;
    }

    public LabelBadge setText(Component t) {
        text = t != null ? t : Component.empty();
        autoSize();
        return this;
    }

    @Override
    public LabelBadge setEditorId(String id) {
        super.setEditorId(id);
        return this;
    }

    /** 父容器设置尺寸时，自动计算文字缩放并更新尺寸 */
    @Override
    protected void updateAbsolutePosition() {
        super.updateAbsolutePosition();
        if (autoScaleBase > 0 && getContentHeight() > 0) {
            textScale = Math.clamp((float) getContentHeight() / autoScaleBase,
                    AUTO_SCALE_MIN, AUTO_SCALE_MAX);
            autoSize();
        }
    }

    // ======== 尺寸 ========

    private void autoSize() {
        var f = Minecraft.getInstance().font;
        int textW = (int) (f.width(text) * textScale);
        int textH = (int) (f.lineHeight * textScale);
        // 图标尺寸 = 徽章内容高度（文字行高），保持四方向 2 格内边距
        if (iconTexture != null) this.iconSize = textH;
        int iconW = iconTexture != null ? iconSize + iconGap : 0;
        this.width = fixedWidth >= 0 ? fixedWidth : iconW + textW + paddingX * 2;
        this.height = Math.max(textH, iconTexture != null ? iconSize : 0) + paddingY * 2;
    }

    // ======== 渲染 ========

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int ax = getAbsoluteX();
        int ay = getAbsoluteY();
        int aw = Math.max(0, width);
        int ah = Math.max(0, height);

        // 背景圆角矩形
        if (bgColor != 0) {
            fillRoundRect(graphics, ax, ay, aw, ah, borderRadius, applyAlpha(bgColor));
        }
        // 边框圆角矩形（外圈填充边框色，内圈填充背景色挖空）
        if (borderWidth > 0) {
            fillRoundRect(graphics, ax, ay, aw, ah, borderRadius, applyAlpha(borderColor));
            int inset = borderWidth;
            fillRoundRect(graphics, ax + inset, ay + inset, Math.max(0, aw - inset * 2),
                    Math.max(0, ah - inset * 2), Math.max(0, borderRadius - inset),
                    applyAlpha(bgColor != 0 ? bgColor : 0xFF000000));
        }

        // 文字 + 前置图标
        var font = Minecraft.getInstance().font;
        int textW = (int) (font.width(text) * textScale);
        int textH = (int) (font.lineHeight * textScale);
        int iconW = iconTexture != null ? iconSize + iconGap : 0;
        int contentH = Math.max(textH, iconTexture != null ? iconSize : 0);

        // 内容起始位置（图标在左，文字紧跟）
        int startX = switch (align) {
            case CENTER -> ax + (aw - iconW - textW) / 2;
            case RIGHT -> ax + aw - iconW - textW - paddingX;
            case LEFT -> ax + paddingX;
        };
        int startY = ay + (ah - contentH) / 2;

        // 图标（完整贴图缩放为 iconSize，垂直居中）
        if (iconTexture != null) {
            int iconY = startY + (contentH - iconSize) / 2;
            int src = iconSrcSize > 0 ? iconSrcSize : iconSize; // 未知源尺寸时按 1:1
            var iconPose = graphics.pose();
            iconPose.pushMatrix();
            iconPose.translate(startX, iconY);
            iconPose.scale(iconSize / (float) src, iconSize / (float) src);
            iconPose.translate(-startX, -iconY);
            graphics.blit(RenderPipelines.GUI_TEXTURED, iconTexture,
                    startX, iconY, 0, 0, src, src, src, src);
            iconPose.popMatrix();
        }

        // 文字（缩放居中）
        int tx = startX + iconW;
        int ty = ay + (ah - textH) / 2;
        var pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(tx, ty);
        pose.scale(textScale, textScale);
        pose.translate(-tx, -ty);
        graphics.text(font, text, tx, ty, applyAlpha(textColor));
        pose.popMatrix();
    }

    /**
     * 填充圆角矩形（逐行扫描线，左右按圆角内缩，上下对称）。
     * @param r 圆角半径
     */
    private void fillRoundRect(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int color) {
        if (w <= 0 || h <= 0) return;
        r = Math.min(r, Math.min(w, h) / 2);
        for (int row = 0; row < h; row++) {
            // 到最近水平边缘的距离（上下对称）
            int d = Math.min(row, h - 1 - row);
            int inset = 0;
            if (d < r) {
                double dy = r - d;
                inset = (int) (r - Math.sqrt(r * r - dy * dy));
            }
            g.fill(x + inset, y + row, x + w - inset, y + row + 1, color);
        }
    }
}

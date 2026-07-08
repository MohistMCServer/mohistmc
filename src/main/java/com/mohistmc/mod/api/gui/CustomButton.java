package com.mohistmc.mod.api.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * 自定义色块按钮 — 纯色背景，支持圆角、发光、阴影等前端风格
 */
@OnlyIn(Dist.CLIENT)
public class CustomButton extends PositionedWidget {

    private Component text;
    private int normalColor;
    private int hoverColor;
    private int textColor = 0xFFFFFFFF;
    private int borderColor = 0;
    private int borderWidth;
    private int borderRadius;            // 圆角半径 px
    private int glowColor;               // 悬停发光颜色（0=不启用）
    private float hoverScale = 1.0f;       // 悬停放大倍率（1.0=不放大）
    private boolean useGradient;            // 是否启用渐变
    private int gradientStart;              // 渐变起始色
    private int gradientEnd;                // 渐变结束色
    private GradientDirection gradientDirection = GradientDirection.TOP_BOTTOM; // 渐变方向
    private Runnable onClick;
    private SoundEvent clickSound;         // 点击音效（null=无声）
    private SoundEvent hoverSound;         // 悬停进入音效（null=无声）
    private boolean wasHovered;            // 上一帧悬停状态，用于检测悬停进入
    private boolean enabled = true;          // 是否启用（禁用后不可点击、无悬停效果）
    @Nullable
    private Component tooltip;               // 悬停提示文字（null=不显示）

    /** 渐变方向枚举 */
    public enum GradientDirection {
        TOP_BOTTOM,     // 上 → 下
        BOTTOM_TOP,     // 下 → 上
        LEFT_RIGHT,     // 左 → 右
        RIGHT_LEFT      // 右 → 左
    }

    public CustomButton(int relX, int relY, int width, int height, Component text, int normalColor) {
        super(relX, relY, width, height);
        this.text = text;
        this.normalColor = normalColor;
        this.hoverColor = darken(normalColor, 0.75f);
    }

    // ======== 链式配置 ========

    /** 修改按钮文字 */
    public CustomButton setText(Component text) {
        this.text = text;
        return this;
    }

    /** 自定义悬停颜色 */
    public CustomButton setHoverColor(int hoverColor) {
        this.hoverColor = hoverColor;
        return this;
    }

    /** 文字颜色 */
    public CustomButton setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    /** 边框 */
    public CustomButton setBorder(int color, int width) {
        this.borderColor = color;
        this.borderWidth = width;
        return this;
    }

    /**
     * 圆角半径
     * @param radius px，0=直角
     */
    public CustomButton setBorderRadius(int radius) {
        this.borderRadius = Math.max(0, radius);
        return this;
    }

    /**
     * 悬停发光颜色
     * @param color 发光颜色（如 0x40FFFFFF），0=不启用
     */
    public CustomButton setGlow(int color) {
        this.glowColor = color;
        return this;
    }

    /**
     * 悬停放大倍率
     * @param scale 放大系数，如 1.08 = 放大 8%；传 1.0 不放大
     */
    public CustomButton setHoverScale(float scale) {
        this.hoverScale = Math.max(1.0f, scale);
        return this;
    }

    /**
     * 设置渐变背景
     * @param startColor 起始颜色 (ARGB)
     * @param endColor   结束颜色 (ARGB)
     * @param direction  渐变方向
     */
    public CustomButton setGradient(int startColor, int endColor, GradientDirection direction) {
        this.useGradient = true;
        this.gradientStart = startColor;
        this.gradientEnd = endColor;
        this.gradientDirection = direction;
        return this;
    }

    /** 关闭渐变，恢复纯色 */
    public CustomButton clearGradient() {
        this.useGradient = false;
        return this;
    }

    /** 点击回调 */
    public CustomButton onClick(Runnable handler) {
        this.onClick = handler;
        return this;
    }

    /**
     * 设置点击音效
     * @param sound 音效事件，传 null 取消音效
     */
    public CustomButton setClickSound(SoundEvent sound) {
        this.clickSound = sound;
        return this;
    }

    /**
     * 设置悬停进入音效（鼠标移入按钮时播放）
     * @param sound 音效事件，传 null 取消音效
     */
    public CustomButton setHoverSound(SoundEvent sound) {
        this.hoverSound = sound;
        return this;
    }

    /**
     * 同时设置点击和悬停音效
     * @param click 点击音效
     * @param hover 悬停进入音效（null=不播放）
     */
    public CustomButton setSounds(SoundEvent click, SoundEvent hover) {
        this.clickSound = click;
        this.hoverSound = hover;
        return this;
    }

    /**
     * 启用默认按钮音效（点击=UI_BUTTON_CLICK，悬停无声）
     */
    public CustomButton withDefaultClickSound() {
        this.clickSound = SoundEvents.UI_BUTTON_CLICK.value();
        return this;
    }

    /**
     * 启用默认悬停音效
     */
    public CustomButton withDefaultHoverSound() {
        this.hoverSound = SoundEvents.UI_BUTTON_CLICK.value();
        return this;
    }

    /**
     * 设置按钮是否启用
     * @param enabled true=可用，false=禁用（变灰、不可点击、无悬停效果）
     */
    public CustomButton setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /** 按钮是否可用 */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 获取当前 tooltip
     */
    @Nullable
    public Component getTooltip() {
        return tooltip;
    }

    /**
     * 设置悬停提示文字（tooltip）
     * <p>鼠标悬停在按钮上时，会在鼠标旁显示一段文字提示</p>
     *
     * @param tooltip 提示文字，传 null 清除提示
     */
    public CustomButton setTooltip(@Nullable Component tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    /**
     * 是否有 tooltip
     */
    public boolean hasTooltip() {
        return tooltip != null;
    }

    @Override
    public CustomButton setRightAnchored(boolean anchored) {
        super.setRightAnchored(anchored);
        return this;
    }

    @Override
    public CustomButton setBottomAnchored(boolean anchored) {
        super.setBottomAnchored(anchored);
        return this;
    }

    @Override
    public CustomButton setAlpha(int alpha) {
        super.setAlpha(alpha);
        return this;
    }

    @Override
    public CustomButton setAlpha(float alpha) {
        super.setAlpha(alpha);
        return this;
    }

    // ======== 内部 ========

    boolean handleClick(MouseButtonEvent event, boolean doubleClick) {
        if (!enabled) return false;
        if (isMouseOver(event.x(), event.y())) {
            if (onClick != null) {
                playSound(clickSound);
                onClick.run();
            }
            return true;
        }
        return false;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = getAbsoluteX();
        int y = getAbsoluteY();
        boolean hovered = enabled && isMouseOver(mouseX, mouseY);

        // 悬停音效：鼠标刚移入按钮时播放
        if (enabled && hovered && !wasHovered) {
            playSound(hoverSound);
        }

        if (enabled && hovered && Float.compare(hoverScale, 1.0f) != 0) {
            // 悬停放大视觉效果 — 以按钮中心为基准缩放
            var pose = graphics.pose();
            pose.pushMatrix();
            float cx = x + width / 2.0f;
            float cy = y + height / 2.0f;
            pose.translate(cx, cy);
            pose.scale(hoverScale, hoverScale);
            pose.translate(-cx, -cy);
            renderContent(graphics, x, y, true);
            pose.popMatrix();
        } else {
            renderContent(graphics, x, y, hovered);
        }
        wasHovered = hovered;

        // 禁用遮罩：半透灰覆盖（保持圆角完整）
        if (!enabled) {
            int r = Math.min(borderRadius, Math.min(width, height) / 2);
            if (r > 0) {
                fillRoundRect(graphics, x, y, width, height, r, 0x80808080);
            } else {
                graphics.fill(x, y, x + width, y + height, 0x80808080);
            }
        }

        // 悬停提示文字（tooltip）— 注册到延迟渲染管线
        if (tooltip != null && hovered) {
            graphics.setTooltipForNextFrame(tooltip, mouseX, mouseY);
        }
    }

    /** 播放音效 */
    private void playSound(SoundEvent sound) {
        if (sound == null) return;
        var mc = Minecraft.getInstance();
        if (mc != null) {
            mc.getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
        }
    }

    /** 渲染按钮主体（不含悬停放大变换） */
    private void renderContent(GuiGraphicsExtractor graphics, int x, int y, boolean hovered) {
        int bgColor = applyAlpha(hovered ? hoverColor : normalColor);
        int r = Math.min(borderRadius, Math.min(width, height) / 2);
        int glowArgb = applyAlpha(glowColor);

        // 1) 悬停发光（紧贴按钮外沿，稍大一圈）
        if (hovered && glowArgb != 0) {
            int glowR = Math.min(r, Math.min(width, height) / 2);
            fillRoundRect(graphics, x - 2, y - 2, width + 4, height + 4, glowR + 1, glowArgb);
        }

        // 2) 边框 + 背景（支持渐变）
        if (useGradient && !hovered) {
            // 渐变背景
            int gs = applyAlpha(gradientStart);
            int ge = applyAlpha(gradientEnd);
            if (r > 0) {
                if (borderWidth > 0) {
                    int bc = applyAlpha(borderColor);
                    fillRoundRect(graphics, x, y, width, height, r, bc);
                    int innerR = Math.max(0, r - borderWidth);
                    fillRoundRectGradient(graphics, x + borderWidth, y + borderWidth,
                            width - borderWidth * 2, height - borderWidth * 2, innerR, gs, ge, gradientDirection);
                } else {
                    fillRoundRectGradient(graphics, x, y, width, height, r, gs, ge, gradientDirection);
                }
            } else {
                if (borderWidth > 0) {
                    int bc = applyAlpha(borderColor);
                    graphics.fill(x, y, x + width, y + borderWidth, bc);
                    graphics.fill(x, y + height - borderWidth, x + width, y + height, bc);
                    graphics.fill(x, y, x + borderWidth, y + height, bc);
                    graphics.fill(x + width - borderWidth, y, x + width, y + height, bc);
                }
                fillGradientRect(graphics, x, y, width, height, gs, ge, gradientDirection);
            }
        } else {
            // 纯色背景（含圆角/直角）
            if (r > 0) {
                if (borderWidth > 0) {
                    int bc = applyAlpha(borderColor);
                    fillRoundRect(graphics, x, y, width, height, r, bc);
                    int innerR = Math.max(0, r - borderWidth);
                    fillRoundRect(graphics, x + borderWidth, y + borderWidth,
                            width - borderWidth * 2, height - borderWidth * 2, innerR, bgColor);
                } else {
                    fillRoundRect(graphics, x, y, width, height, r, bgColor);
                }
            } else {
                if (borderWidth > 0) {
                    int bc = applyAlpha(borderColor);
                    graphics.fill(x, y, x + width, y + borderWidth, bc);
                    graphics.fill(x, y + height - borderWidth, x + width, y + height, bc);
                    graphics.fill(x, y, x + borderWidth, y + height, bc);
                    graphics.fill(x + width - borderWidth, y, x + width, y + height, bc);
                }
                graphics.fill(x, y, x + width, y + height, bgColor);
            }
        }

        // 4) 居中文字（文字颜色应用透明度）
        var font = Minecraft.getInstance().font;
        int textX = x + (width - font.width(text)) / 2;
        int textY = y + (height - font.lineHeight) / 2;
        graphics.text(font, text, textX, textY, applyAlpha(textColor));
    }

    // ======== 圆角矩形渲染 ========

    /** 绘制填充圆角矩形 */
    private static void fillRoundRect(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int color) {
        if (r <= 0 || w <= 0 || h <= 0) {
            if (w > 0 && h > 0) g.fill(x, y, x + w, y + h, color);
            return;
        }
        r = Math.min(r, Math.min(w / 2, h / 2));

        // 1) 中间主体（去除左右圆弧区）
        g.fill(x + r, y, x + w - r, y + h, color);
        // 2) 左右边条（去除上下圆弧区）
        g.fill(x, y + r, x + r, y + h - r, color);
        g.fill(x + w - r, y + r, x + w, y + h - r, color);

        // 3) 四角圆弧（逐行绘制近似 quarter-circle）
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int px = (int) Math.sqrt(r * r - dy * dy);
            px = Math.min(px, r);

            // TL
            if (px > 0) g.fill(x + r - px, y + i, x + r, y + i + 1, color);
            // TR
            if (px > 0) g.fill(x + w - r, y + i, x + w - r + px, y + i + 1, color);
            // BL
            if (px > 0) g.fill(x + r - px, y + h - i - 1, x + r, y + h - i, color);
            // BR
            if (px > 0) g.fill(x + w - r, y + h - i - 1, x + w - r + px, y + h - i, color);
        }
    }

    // ======== 渐变渲染 ========

    /** 颜色插值（ARGB） */
    private static int lerpColor(int c1, int c2, float t) {
        t = Math.min(1f, Math.max(0f, t));
        int a = (int) (((c1 >>> 24) * (1 - t) + (c2 >>> 24) * t));
        int r = (int) ((((c1 >> 16) & 0xFF) * (1 - t) + ((c2 >> 16) & 0xFF) * t));
        int g = (int) ((((c1 >> 8) & 0xFF) * (1 - t) + ((c2 >> 8) & 0xFF) * t));
        int b = (int) (((c1 & 0xFF) * (1 - t) + (c2 & 0xFF) * t));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * 根据渐变方向和进度获取颜色
     * @param progress 当前步进索引
     * @param total    总步数
     */
    private int getGradientColor(int start, int end, int progress, int total, GradientDirection dir) {
        float t;
        switch (dir) {
            case TOP_BOTTOM:
                t = (float) progress / (total - 1);
                break;
            case BOTTOM_TOP:
                t = 1f - (float) progress / (total - 1);
                break;
            case LEFT_RIGHT:
                t = (float) progress / (total - 1);
                break;
            case RIGHT_LEFT:
                t = 1f - (float) progress / (total - 1);
                break;
            default:
                t = (float) progress / (total - 1);
        }
        return lerpColor(start, end, t);
    }

    /** 绘制纯色渐变矩形（无圆角） */
    private static void fillGradientRect(GuiGraphicsExtractor g, int x, int y, int w, int h, int start, int end, GradientDirection dir) {
        if (w <= 0 || h <= 0) return;
        switch (dir) {
            case TOP_BOTTOM -> {
                if (h == 1) { g.fill(x, y, x + w, y + 1, start); return; }
                for (int i = 0; i < h; i++)
                    g.fill(x, y + i, x + w, y + i + 1, lerpColor(start, end, (float) i / (h - 1)));
            }
            case BOTTOM_TOP -> {
                if (h == 1) { g.fill(x, y, x + w, y + 1, end); return; }
                for (int i = 0; i < h; i++)
                    g.fill(x, y + i, x + w, y + i + 1, lerpColor(start, end, 1f - (float) i / (h - 1)));
            }
            case LEFT_RIGHT -> {
                if (w == 1) { g.fill(x, y, x + 1, y + h, start); return; }
                for (int i = 0; i < w; i++)
                    g.fill(x + i, y, x + i + 1, y + h, lerpColor(start, end, (float) i / (w - 1)));
            }
            case RIGHT_LEFT -> {
                if (w == 1) { g.fill(x, y, x + 1, y + h, end); return; }
                for (int i = 0; i < w; i++)
                    g.fill(x + i, y, x + i + 1, y + h, lerpColor(start, end, 1f - (float) i / (w - 1)));
            }
        }
    }

    /** 绘制渐变圆角矩形（逐行/逐列渲染，保持角落圆弧正确） */
    private void fillRoundRectGradient(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int start, int end, GradientDirection dir) {
        if (r <= 0 || w <= 0 || h <= 0) {
            fillGradientRect(g, x, y, w, h, start, end, dir);
            return;
        }
        r = Math.min(r, Math.min(w / 2, h / 2));
        boolean rowBased = dir == GradientDirection.TOP_BOTTOM || dir == GradientDirection.BOTTOM_TOP;

        if (rowBased) {
            // 逐行渲染：每行按渐变方向取色，行内按圆角裁剪左右端
            for (int row = 0; row < h; row++) {
                int color = getGradientColor(start, end, row, h, dir);
                int left, right;
                if (row < r) {
                    double dy = r - row - 0.5;
                    int px = (int) Math.sqrt(r * r - dy * dy);
                    px = Math.min(px, r);
                    left = x + (r - px);
                    right = x + w - (r - px);
                } else if (row >= h - r) {
                    int br = h - row - 1;
                    double dy = r - br - 0.5;
                    int px = (int) Math.sqrt(r * r - dy * dy);
                    px = Math.min(px, r);
                    left = x + (r - px);
                    right = x + w - (r - px);
                } else {
                    left = x;
                    right = x + w;
                }
                if (right > left) g.fill(left, y + row, right, y + row + 1, color);
            }
        } else {
            // 逐列渲染：每列按渐变方向取色，列内按圆角裁剪上下端
            for (int col = 0; col < w; col++) {
                int color = getGradientColor(start, end, col, w, dir);
                int top, bottom;
                if (col < r) {
                    double dx = r - col - 0.5;
                    int py = (int) Math.sqrt(r * r - dx * dx);
                    py = Math.min(py, r);
                    top = y + (r - py);
                    bottom = y + h - (r - py);
                } else if (col >= w - r) {
                    int br = w - col - 1;
                    double dx = r - br - 0.5;
                    int py = (int) Math.sqrt(r * r - dx * dx);
                    py = Math.min(py, r);
                    top = y + (r - py);
                    bottom = y + h - (r - py);
                } else {
                    top = y;
                    bottom = y + h;
                }
                if (bottom > top) g.fill(x + col, top, x + col + 1, bottom, color);
            }
        }
    }

    /** 变暗（乘系数），用于默认 hover 色 */
    private static int darken(int color, float factor) {
        int a = color >>> 24;
        int r = (int) (((color >> 16) & 0xFF) * factor);
        int g = (int) (((color >> 8) & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}

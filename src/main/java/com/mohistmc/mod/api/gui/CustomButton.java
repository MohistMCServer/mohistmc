package com.mohistmc.mod.api.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class CustomButton extends PositionedWidget {

    private Component text;
    private int normalColor;
    private int hoverColor;
    private int textColor = 0xFFFFFFFF;
    @Nullable
    private static Font defaultFont;
    private int borderWidth;
    private int borderColor;
    private int borderRadius;
    private int glowColor;
    private float hoverScale = 1.0f;
    private boolean useGradient;
    private int gradientStart;
    private int gradientEnd;
    private Runnable onClick;
    private GradientDirection gradientDirection = GradientDirection.TOP_BOTTOM;
    private SoundEvent clickSound;
    private SoundEvent hoverSound;
    private boolean wasHovered;
    private boolean enabled = true;
    @Nullable
    private Component tooltip;
    @Nullable
    private Font customFont;

    @Nullable
    public static Font getDefaultFont() {
        return defaultFont;
    }

    public static void setDefaultFont(@Nullable Font f) {
        defaultFont = f;
    }

    private static void fillRoundRect(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int c) {
        if (r <= 0 || w <= 0 || h <= 0) {
            if (w > 0 && h > 0) g.fill(x, y, x + w, y + h, c);
            return;
        }
        r = Math.min(r, Math.min(w / 2, h / 2));
        g.fill(x + r, y, x + w - r, y + h, c);
        g.fill(x, y + r, x + r, y + h - r, c);
        g.fill(x + w - r, y + r, x + w, y + h - r, c);
        for (int i = 0; i < r; i++) {
            double d = r - i - 0.5;
            int p = (int) Math.sqrt(r * r - d * d);
            p = Math.min(p, r);
            if (p <= 0) continue;
            g.fill(x + r - p, y + i, x + r, y + i + 1, c);
            g.fill(x + w - r, y + i, x + w - r + p, y + i + 1, c);
            g.fill(x + r - p, y + h - i - 1, x + r, y + h - i, c);
            g.fill(x + w - r, y + h - i - 1, x + w - r + p, y + h - i, c);
        }
    }

    public CustomButton(int relX, int relY, int width, int height, Component text, int normalColor) {
        super(relX, relY, width, height);
        this.text = text;
        this.normalColor = normalColor;
        this.hoverColor = darken(normalColor, 0.75f);
    }

    private static int lerpColor(int c1, int c2, float t) {
        t = Math.min(1f, Math.max(0f, t));
        return ((int) ((c1 >>> 24) * (1 - t) + (c2 >>> 24) * t) << 24) | ((int) (((c1 >> 16) & 0xFF) * (1 - t) + ((c2 >> 16) & 0xFF) * t) << 16) | ((int) (((c1 >> 8) & 0xFF) * (1 - t) + ((c2 >> 8) & 0xFF) * t) << 8) | (int) ((c1 & 0xFF) * (1 - t) + (c2 & 0xFF) * t);
    }

    private static void fillGradientRect(GuiGraphicsExtractor g, int x, int y, int w, int h, int s, int e, GradientDirection d) {
        if (w <= 0 || h <= 0) return;
        switch (d) {
            case TOP_BOTTOM -> {
                for (int i = 0; i < h; i++) g.fill(x, y + i, x + w, y + i + 1, lerpColor(s, e, (float) i / (h - 1)));
            }
            case BOTTOM_TOP -> {
                for (int i = 0; i < h; i++)
                    g.fill(x, y + i, x + w, y + i + 1, lerpColor(s, e, 1f - (float) i / (h - 1)));
            }
            case LEFT_RIGHT -> {
                for (int i = 0; i < w; i++) g.fill(x + i, y, x + i + 1, y + h, lerpColor(s, e, (float) i / (w - 1)));
            }
            case RIGHT_LEFT -> {
                for (int i = 0; i < w; i++)
                    g.fill(x + i, y, x + i + 1, y + h, lerpColor(s, e, 1f - (float) i / (w - 1)));
            }
        }
    }

    private static int darken(int color, float factor) {
        return (color >>> 24 << 24) | ((int) (((color >> 16) & 0xFF) * factor) << 16) | ((int) (((color >> 8) & 0xFF) * factor) << 8) | (int) ((color & 0xFF) * factor);
    }

    public CustomButton setText(Component t) {
        text = t;
        return this;
    }

    public CustomButton setHoverColor(int c) {
        hoverColor = c;
        return this;
    }

    public CustomButton setTextColor(int c) {
        textColor = c;
        return this;
    }

    public CustomButton setFont(@Nullable Font f) {
        customFont = f;
        return this;
    }

    public CustomButton setBorder(int c, int w) {
        borderColor = c;
        borderWidth = w;
        return this;
    }

    public CustomButton setBorderRadius(int r) {
        borderRadius = Math.max(0, r);
        return this;
    }

    public CustomButton setGlow(int c) {
        glowColor = c;
        return this;
    }

    public CustomButton setHoverScale(float s) {
        hoverScale = Math.max(1.0f, s);
        return this;
    }

    public CustomButton setGradient(int s, int e, GradientDirection d) {
        useGradient = true;
        gradientStart = s;
        gradientEnd = e;
        gradientDirection = d;
        return this;
    }

    public CustomButton clearGradient() {
        useGradient = false;
        return this;
    }

    public CustomButton onClick(Runnable h) {
        this.onClick = h;
        return this;
    }

    public CustomButton setClickSound(SoundEvent s) {
        clickSound = s;
        return this;
    }

    public CustomButton setHoverSound(SoundEvent s) {
        hoverSound = s;
        return this;
    }

    public CustomButton setSounds(SoundEvent c, SoundEvent h) {
        clickSound = c;
        hoverSound = h;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public CustomButton withDefaultClickSound() {
        clickSound = SoundEvents.UI_BUTTON_CLICK.value();
        return this;
    }

    @Nullable
    public Component getTooltip() {
        return tooltip;
    }

    public boolean hasTooltip() {
        return tooltip != null;
    }

    public CustomButton withDefaultHoverSound() {
        hoverSound = SoundEvents.UI_BUTTON_CLICK.value();
        return this;
    }

    public CustomButton setEnabled(boolean e) {
        enabled = e;
        return this;
    }

    public CustomButton setTooltip(@Nullable Component t) {
        tooltip = t;
        return this;
    }

    @Override
    public CustomButton setRightAnchored(boolean a) {
        super.setRightAnchored(a);
        return this;
    }

    @Override
    public CustomButton setBottomAnchored(boolean a) {
        super.setBottomAnchored(a);
        return this;
    }

    @Override
    public CustomButton setAlpha(int a) {
        super.setAlpha(a);
        return this;
    }

    @Override
    public CustomButton setAlpha(float a) {
        super.setAlpha(a);
        return this;
    }

    boolean handleClick(MouseButtonEvent e, boolean dbl) {
        if (!enabled) return false;
        if (isMouseOver(e.x(), e.y())) {
            if (onClick != null) {
                playSound(clickSound);
                onClick.run();
            }
            return true;
        }
        return false;
    }

    @Override
    public void render(GuiGraphicsExtractor g, int mx, int my, float pt) {
        int x = getAbsoluteX(), y = getAbsoluteY();
        boolean hovered = enabled && isMouseOver(mx, my);
        if (enabled && hovered && !wasHovered) playSound(hoverSound);
        if (enabled && hovered && Float.compare(hoverScale, 1.0f) != 0) {
            var pose = g.pose();
            pose.pushMatrix();
            float cx = x + width / 2f, cy = y + height / 2f;
            pose.translate(cx, cy);
            pose.scale(hoverScale, hoverScale);
            pose.translate(-cx, -cy);
            renderContent(g, x, y, true);
            pose.popMatrix();
        } else {
            renderContent(g, x, y, hovered);
        }
        wasHovered = hovered;
        if (!enabled) {
            int r = Math.min(borderRadius, Math.min(width, height) / 2);
            if (r > 0) fillRoundRect(g, x, y, width, height, r, 0x80808080);
            else g.fill(x, y, x + width, y + height, 0x80808080);
        }
        if (tooltip != null && hovered) g.setTooltipForNextFrame(tooltip, mx, my);
    }

    private void playSound(SoundEvent s) {
        if (s == null) return;
        var mc = Minecraft.getInstance();
        if (mc != null) mc.getSoundManager().play(SimpleSoundInstance.forUI(s, 1.0F));
    }

    private void renderContent(GuiGraphicsExtractor g, int x, int y, boolean hovered) {
        int bg = applyAlpha(hovered ? hoverColor : normalColor);
        int r = Math.min(borderRadius, Math.min(width, height) / 2);
        int gl = applyAlpha(glowColor);
        if (hovered && gl != 0) {
            int gr = Math.min(r, Math.min(width, height) / 2);
            fillRoundRect(g, x - 2, y - 2, width + 4, height + 4, gr + 1, gl);
        }
        if (useGradient && !hovered) {
            int gs = applyAlpha(gradientStart), ge = applyAlpha(gradientEnd);
            if (r > 0) {
                if (borderWidth > 0) {
                    int bc = applyAlpha(borderColor);
                    fillRoundRect(g, x, y, width, height, r, bc);
                    int ir = Math.max(0, r - borderWidth);
                    fillRoundRectGradient(g, x + borderWidth, y + borderWidth, width - borderWidth * 2, height - borderWidth * 2, ir, gs, ge, gradientDirection);
                } else fillRoundRectGradient(g, x, y, width, height, r, gs, ge, gradientDirection);
            } else {
                if (borderWidth > 0) {
                    int bc = applyAlpha(borderColor);
                    g.fill(x, y, x + width, y + borderWidth, bc);
                    g.fill(x, y + height - borderWidth, x + width, y + height, bc);
                    g.fill(x, y, x + borderWidth, y + height, bc);
                    g.fill(x + width - borderWidth, y, x + width, y + height, bc);
                }
                fillGradientRect(g, x, y, width, height, gs, ge, gradientDirection);
            }
        } else {
            if (r > 0) {
                if (borderWidth > 0) {
                    int bc = applyAlpha(borderColor);
                    fillRoundRect(g, x, y, width, height, r, bc);
                    int ir = Math.max(0, r - borderWidth);
                    fillRoundRect(g, x + borderWidth, y + borderWidth, width - borderWidth * 2, height - borderWidth * 2, ir, bg);
                } else fillRoundRect(g, x, y, width, height, r, bg);
            } else {
                if (borderWidth > 0) {
                    int bc = applyAlpha(borderColor);
                    g.fill(x, y, x + width, y + borderWidth, bc);
                    g.fill(x, y + height - borderWidth, x + width, y + height, bc);
                    g.fill(x, y, x + borderWidth, y + height, bc);
                    g.fill(x + width - borderWidth, y, x + width, y + height, bc);
                }
                g.fill(x, y, x + width, y + height, bg);
            }
        }
        Font font = customFont != null ? customFont : defaultFont != null ? defaultFont : Minecraft.getInstance().font;
        int tx = x + (width - font.width(text)) / 2, ty = y + (height - font.lineHeight) / 2;
        g.text(font, text, tx, ty, applyAlpha(textColor));
    }

    private int getGradientColor(int s, int e, int p, int t, GradientDirection d) {
        return lerpColor(s, e, switch (d) {
            case TOP_BOTTOM, LEFT_RIGHT -> (float) p / (t - 1);
            case BOTTOM_TOP, RIGHT_LEFT -> 1f - (float) p / (t - 1);
        });
    }

    private void fillRoundRectGradient(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int s, int e, GradientDirection d) {
        if (r <= 0 || w <= 0 || h <= 0) {
            fillGradientRect(g, x, y, w, h, s, e, d);
            return;
        }
        r = Math.min(r, Math.min(w / 2, h / 2));
        boolean rb = d == GradientDirection.TOP_BOTTOM || d == GradientDirection.BOTTOM_TOP;
        if (rb) {
            for (int row = 0; row < h; row++) {
                int c = getGradientColor(s, e, row, h, d);
                int l, r2;
                if (row < r) {
                    double dd = r - row - 0.5;
                    int p = (int) Math.sqrt(r * r - dd * dd);
                    p = Math.min(p, r);
                    l = x + r - p;
                    r2 = x + w - r + p;
                } else if (row >= h - r) {
                    int br = h - row - 1;
                    double dd = r - br - 0.5;
                    int p = (int) Math.sqrt(r * r - dd * dd);
                    p = Math.min(p, r);
                    l = x + r - p;
                    r2 = x + w - r + p;
                } else {
                    l = x;
                    r2 = x + w;
                }
                if (r2 > l) g.fill(l, y + row, r2, y + row + 1, c);
            }
        } else {
            for (int col = 0; col < w; col++) {
                int c = getGradientColor(s, e, col, w, d);
                int t, b;
                if (col < r) {
                    double dd = r - col - 0.5;
                    int p = (int) Math.sqrt(r * r - dd * dd);
                    p = Math.min(p, r);
                    t = y + r - p;
                    b = y + h - r + p;
                } else if (col >= w - r) {
                    int br = w - col - 1;
                    double dd = r - br - 0.5;
                    int p = (int) Math.sqrt(r * r - dd * dd);
                    p = Math.min(p, r);
                    t = y + r - p;
                    b = y + h - r + p;
                } else {
                    t = y;
                    b = y + h;
                }
                if (b > t) g.fill(x + col, t, x + col + 1, b, c);
            }
        }
    }

    public enum GradientDirection {TOP_BOTTOM, BOTTOM_TOP, LEFT_RIGHT, RIGHT_LEFT}
}

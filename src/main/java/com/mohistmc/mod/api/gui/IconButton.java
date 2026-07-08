package com.mohistmc.mod.api.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * 图标按钮 — 纯纹理 + 背景，无文字，支持 tooltip 与点击。
 * 纹理需调用 {@link #setTexture(Identifier)} 设置，未设置时仅渲染背景色块。
 */
public class IconButton extends PositionedWidget {

    @Nullable
    private Identifier texture;
    private int bgColor = 0x00000000;
    private int hoverBgColor = 0x55FFFFFF;
    @Nullable
    private Component tooltip;
    @Nullable
    private Runnable onClick;
    private boolean enabled = true;
    private boolean wasHovered;

    public IconButton(int relX, int relY, int size) {
        super(relX, relY, size, size);
    }

    public IconButton(int relX, int relY, int width, int height) {
        super(relX, relY, width, height);
    }

    /** 设置纹理（用户在 resources 中添加对应路径的 png） */
    public IconButton setTexture(@Nullable Identifier tex) {
        this.texture = tex;
        return this;
    }

    /** 默认背景色（hover 前），0=完全透明 */
    public IconButton setBgColor(int color) {
        this.bgColor = color;
        return this;
    }

    /** 悬停背景色 */
    public IconButton setHoverBgColor(int color) {
        this.hoverBgColor = color;
        return this;
    }

    public IconButton setTooltip(@Nullable Component tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public IconButton onClick(@Nullable Runnable handler) {
        this.onClick = handler;
        return this;
    }

    public IconButton setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean hasTooltip() {
        return tooltip != null;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = getAbsoluteX();
        int y = getAbsoluteY();
        boolean hovered = enabled && isMouseOver(mouseX, mouseY);

        // 背景
        int color = hovered ? hoverBgColor : bgColor;
        if ((color >>> 24) != 0) {
            graphics.fill(x, y, x + width, y + height, color);
        }

        // 纹理
        if (texture != null) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, width, height, width, height);
        }

        // tooltip
        if (tooltip != null && hovered) {
            graphics.setTooltipForNextFrame(tooltip, mouseX, mouseY);
        }

        wasHovered = hovered;
    }

    boolean handleClick(MouseButtonEvent event, boolean doubleClick) {
        if (!enabled) return false;
        if (isMouseOver(event.x(), event.y())) {
            if (onClick != null) {
                onClick.run();
                return true;
            }
        }
        return false;
    }
}

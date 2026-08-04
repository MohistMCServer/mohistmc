package com.mohistmc.mod.api.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * 面板组件 — 带背景色/背景图片的矩形区域，可包含子组件、响应点击
 */
@OnlyIn(Dist.CLIENT)
public class Panel extends PositionedWidget {

    /** 布局方向 */
    public enum LayoutDirection {
        FREE,       // 手动定位（默认）
        VERTICAL,   // 从上往下排列
        HORIZONTAL  // 从左往右排列
    }

    /** 背景图片填充模式 */
    public enum ImageFill {
        STRETCH,    // 拉伸填满整个面板（默认）
        REPEAT,     // 重复平铺
        CENTER      // 居中原样显示，四周留空
    }

    protected int backgroundColor;
    @Nullable
    protected Identifier backgroundTexture;
    protected ImageFill imageFill = ImageFill.STRETCH;
    protected int borderColor = 0xFFFFFFFF;
    protected int borderWidth;
    private LayoutDirection layoutDirection = LayoutDirection.FREE;
    private int layoutGap;
    private Consumer<Panel> clickHandler;
    private final List<PositionedWidget> children = new ArrayList<>();

    /**
     * @param relX            相对屏幕左侧的 X
     * @param relY            相对屏幕顶部的 Y
     * @param width           面板宽度
     * @param height          面板高度
     * @param backgroundColor 背景色 ARGB
     */
    public Panel(int relX, int relY, int width, int height, int backgroundColor) {
        super(relX, relY, width, height);
        this.backgroundColor = backgroundColor;
    }

    /**
     * @param relX   相对屏幕左侧的 X
     * @param relY   相对屏幕顶部的 Y
     * @param width  面板宽度
     * @param height 面板高度
     * @param texture 背景纹理
     */
    public Panel(int relX, int relY, int width, int height, Identifier texture) {
        super(relX, relY, width, height);
        this.backgroundTexture = texture;
        this.backgroundColor = 0;
    }

    // ======== 链式配置 ========

    public Panel setBorder(int color, int width) {
        this.borderColor = color;
        this.borderWidth = width;
        return this;
    }

    /** 设置点击回调，参数为面板自身 */
    public Panel onClick(Consumer<Panel> handler) {
        this.clickHandler = handler;
        return this;
    }

    /** 右锚定：relX 改为距右边缘的距离（返回 Panel 保持链式类型） */
    @Override
    public Panel setRightAnchored(boolean anchored) {
        super.setRightAnchored(anchored);
        return this;
    }

    /** 下锚定：relY 改为距下边缘的距离（返回 Panel 保持链式类型） */
    @Override
    public Panel setBottomAnchored(boolean anchored) {
        super.setBottomAnchored(anchored);
        return this;
    }

    /** 设置布局模式
     * @param direction 排列方向
     * @param gap       子组件间距（px）
     */
    public Panel setLayout(LayoutDirection direction, int gap) {
        this.layoutDirection = direction != null ? direction : LayoutDirection.FREE;
        this.layoutGap = Math.max(0, gap);
        return this;
    }

    /** 弹性增长因子（在父容器布局中占据剩余空间） */
    @Override
    public Panel setFlexGrow(int grow) {
        super.setFlexGrow(grow);
        return this;
    }

    /** 设置背景纹理（为 null 则仅使用纯色背景） */
    public Panel setBackgroundTexture(@Nullable Identifier texture) {
        this.backgroundTexture = texture;
        return this;
    }

    /** 设置背景图片填充模式 */
    public Panel setImageFill(ImageFill fill) {
        this.imageFill = fill != null ? fill : ImageFill.STRETCH;
        return this;
    }

    /** 设置透明度 (0-255)，返回 Panel 保持链式类型 */
    @Override
    public Panel setAlpha(int alpha) {
        super.setAlpha(alpha);
        return this;
    }

    /** 设置透明度 (0.0 ~ 1.0) */
    @Override
    public Panel setAlpha(float alpha) {
        super.setAlpha(alpha);
        return this;
    }

    // ======== 子组件管理 ========

    /**
     * 添加一个 PositionedWidget 子组件
     * <p>FREE 布局时 relX/relY 相对于面板左上角；有布局时由布局自动排列</p>
     */
    public void addChild(PositionedWidget child) {
        children.add(child);
        if (layoutDirection != LayoutDirection.FREE) {
            layoutChildren();
        } else {
            child.setScreenPos(getAbsoluteX(), getAbsoluteY(), this.width, this.height);
        }
    }

    /**
     * 递归查找所有子组件（含孙子辈）中的可渲染组件
     */
    List<PositionedWidget> collectAllChildren() {
        List<PositionedWidget> all = new ArrayList<>();
        for (var child : children) {
            all.add(child);
            if (child instanceof Panel p) {
                all.addAll(p.collectAllChildren());
            }
        }
        return all;
    }

    // ======== 内部 ========

    @Override
    void setScreenPos(int left, int top, int contentWidth, int contentHeight) {
        super.setScreenPos(left, top, contentWidth, contentHeight);
        if (layoutDirection != LayoutDirection.FREE) {
            layoutChildren();
        } else {
            for (var child : children) {
                child.setScreenPos(getAbsoluteX(), getAbsoluteY(), this.width, this.height);
            }
        }
    }

    /** 执行布局：根据方向排列所有子组件（溢出时自动按比例收缩） */
    private void layoutChildren() {
        if (children.isEmpty()) return;
        int px = getAbsoluteX() + borderWidth;
        int py = getAbsoluteY() + borderWidth;
        int innerW = Math.max(0, width - borderWidth * 2);
        int innerH = Math.max(0, height - borderWidth * 2);

        if (layoutDirection == LayoutDirection.VERTICAL) {
            int naturalH = layoutGap;
            int totalGrow = 0;
            for (var c : children) {
                if (c.flexGrow > 0) totalGrow += c.flexGrow;
                else naturalH += c.height + layoutGap;
            }

            int[] childHeights = new int[children.size()];
            if (totalGrow > 0 && naturalH <= innerH) {
                // flex 子组件平分剩余空间
                int remain = Math.max(0, innerH - naturalH);
                int growUnit = remain / totalGrow;
                for (int i = 0; i < children.size(); i++) {
                    var c = children.get(i);
                    childHeights[i] = c.flexGrow > 0 ? c.flexGrow * growUnit : c.height;
                }
            } else {
                // 溢出或无 flex：所有子组件等比收缩
                float totalH = 0;
                for (var c : children) totalH += c.height;
                float gaps = layoutGap * (children.size() + 1);
                float scale = totalH > 0 ? Math.min(1f, (float) Math.max(0, innerH - gaps) / totalH) : 1f;
                for (int i = 0; i < children.size(); i++) {
                    childHeights[i] = Math.max(1, (int) (children.get(i).height * scale));
                }
            }

            int curY = py + layoutGap;
            for (int i = 0; i < children.size(); i++) {
                var child = children.get(i);
                int childH = childHeights[i];
                child.width = innerW;
                child.height = childH;
                child.relativeX = 0;
                child.relativeY = curY - py;
                child.setScreenPos(px, py, innerW, childH);
                curY += childH + layoutGap;
            }
        } else if (layoutDirection == LayoutDirection.HORIZONTAL) {
            int naturalW = layoutGap;
            int totalGrow = 0;
            for (var c : children) {
                if (c.flexGrow > 0) totalGrow += c.flexGrow;
                else naturalW += c.width + layoutGap;
            }

            int[] childWidths = new int[children.size()];
            if (totalGrow > 0 && naturalW <= innerW) {
                // flex 子组件平分剩余宽度
                int remain = Math.max(0, innerW - naturalW);
                int growUnit = remain / totalGrow;
                for (int i = 0; i < children.size(); i++) {
                    var c = children.get(i);
                    childWidths[i] = c.flexGrow > 0 ? c.flexGrow * growUnit : c.width;
                }
            } else {
                // 溢出或无 flex：所有子组件等比收缩
                float totalW = 0;
                for (var c : children) totalW += c.width;
                float gaps = layoutGap * (children.size() + 1);
                float scale = totalW > 0 ? Math.min(1f, (float) Math.max(0, innerW - gaps) / totalW) : 1f;
                for (int i = 0; i < children.size(); i++) {
                    childWidths[i] = Math.max(1, (int) (children.get(i).width * scale));
                }
            }

            int curX = px + layoutGap;
            for (int i = 0; i < children.size(); i++) {
                var child = children.get(i);
                int childW = childWidths[i];
                child.width = childW;
                child.height = innerH;
                child.relativeX = curX - px;
                child.relativeY = 0;
                child.setScreenPos(px, py, childW, innerH);
                curX += childW + layoutGap;
            }
        }
    }

    /** 检查是否被点击，触发回调 */
    boolean handleClick(MouseButtonEvent event, boolean doubleClick) {
        if (isMouseOver(event.x(), event.y()) && clickHandler != null) {
            clickHandler.accept(this);
            return true;
        }
        return false;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = getAbsoluteX();
        int y = getAbsoluteY();

        // —— 背景 ——

        // 1) 纯色背景层（应用透明度）
        if (backgroundColor != 0) {
            graphics.fill(x, y, x + width, y + height, applyAlpha(backgroundColor));
        }

        // 2) 纹理背景层（应用透明度）
        if (backgroundTexture != null) {
            int a = applyAlpha(0xFFFFFFFF) >>> 24; // 提取混合后的 alpha
            // 纹理渲染不支持直接 alpha 混合，用透明预乘纯色垫底
            if (a < 255 && a > 0) {
                // 用透明度预乘的半透明黑垫底，近似整体透明度效果
                int bgWithAlpha = (a << 24) | 0x000000;
                graphics.fill(x, y, x + width, y + height, bgWithAlpha);
            }
            if (a > 0) {
                renderTexture(graphics, x, y);
            }
        }

        // 3) 边框（应用透明度）
        if (borderWidth > 0) {
            int bc = applyAlpha(borderColor);
            graphics.fill(x, y, x + width, y + borderWidth, bc);
            graphics.fill(x, y + height - borderWidth, x + width, y + height, bc);
            graphics.fill(x, y, x + borderWidth, y + height, bc);
            graphics.fill(x + width - borderWidth, y, x + width, y + height, bc);
        }

        // 子组件
        for (var child : children) {
            child.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    /** 根据填充模式渲染背景纹理 */
    private void renderTexture(GuiGraphicsExtractor graphics, int x, int y) {
        switch (imageFill) {
            case STRETCH -> graphics.blit(RenderPipelines.GUI_TEXTURED, backgroundTexture,
                    x, y, 0, 0, width, height, width, height);
            case CENTER -> {
                // 假设纹理宽高等于面板原始目标大小（在 CENTER 模式下取纹理原始大小）
                // 如果 width/height 就是面板大小，texture 可能大于面板，取面板尺寸适配
                int tw = Math.min(width, height); // 近似：当作方形处理
                int tx = x + (width - tw) / 2;
                int ty = y + (height - tw) / 2;
                graphics.blit(RenderPipelines.GUI_TEXTURED, backgroundTexture,
                        tx, ty, 0, 0, tw, tw, tw, tw);
            }
            case REPEAT -> {
                // 平铺：每次渲染一个 256×256 块
                int tileSize = 256;
                for (int ty2 = y; ty2 < y + height; ty2 += tileSize) {
                    for (int tx2 = x; tx2 < x + width; tx2 += tileSize) {
                        int tw2 = Math.min(tileSize, x + width - tx2);
                        int th2 = Math.min(tileSize, y + height - ty2);
                        graphics.blit(RenderPipelines.GUI_TEXTURED, backgroundTexture,
                                tx2, ty2, 0, 0, tw2, th2, tw2, th2);
                    }
                }
            }
        }
    }
}

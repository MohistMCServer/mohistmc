package com.mohistmc.mod.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class EnhancedScreen extends Screen {
    /** 背景纹理，为 null 表示不使用纹理背景 */
    @Nullable
    protected final Identifier BACKGROUND;
    /** 纯色背景色（ARGB），-1 表示不使用纯色背景 */
    protected final int backgroundColor;
    protected int leftPos, topPos;        // GUI 左上角
    protected int imageWidth, imageHeight; // GUI 大小（-1 表示全屏）

    private final List<PositionedWidget> widgets = new ArrayList<>();
    private final List<Modal> modals = new ArrayList<>();

    // ———— 全屏构造（无宽高） ————

    /** 纯色背景，全屏 */
    protected EnhancedScreen(Component title, int backgroundColor) {
        super(title);
        this.BACKGROUND = null;
        this.backgroundColor = backgroundColor;
        this.imageWidth = -1;
        this.imageHeight = -1;
    }

    /** 纹理背景，全屏 */
    protected EnhancedScreen(Component title, @org.jspecify.annotations.Nullable Identifier background) {
        super(title);
        this.BACKGROUND = background;
        this.backgroundColor = -1;
        this.imageWidth = -1;
        this.imageHeight = -1;
    }

    /** 纹理 + 纯色混合，全屏 */
    protected EnhancedScreen(Component title, @Nullable Identifier background, int backgroundColor) {
        super(title);
        this.BACKGROUND = background;
        this.backgroundColor = backgroundColor;
        this.imageWidth = -1;
        this.imageHeight = -1;
    }

    // ———— 指定宽高 ————

    /** 纹理背景 + 指定宽高 */
    protected EnhancedScreen(Component title, Identifier background, int width, int height) {
        super(title);
        this.BACKGROUND = background;
        this.backgroundColor = -1;
        this.imageWidth = width;
        this.imageHeight = height;
    }

    /** 纯色背景 + 指定宽高 */
    protected EnhancedScreen(Component title, int backgroundColor, int width, int height) {
        super(title);
        this.BACKGROUND = null;
        this.backgroundColor = backgroundColor;
        this.imageWidth = width;
        this.imageHeight = height;
    }

    /** 纹理 + 纯色 + 指定宽高 */
    protected EnhancedScreen(Component title, @Nullable Identifier background, int backgroundColor, int width, int height) {
        super(title);
        this.BACKGROUND = background;
        this.backgroundColor = backgroundColor;
        this.imageWidth = width;
        this.imageHeight = height;
    }

    /** 返回实际 GUI 宽度（全屏时返回当前屏幕宽度） */
    protected int getImageWidth() {
        return imageWidth == -1 ? this.width : imageWidth;
    }

    /** 返回实际 GUI 高度（全屏时返回当前屏幕高度） */
    protected int getImageHeight() {
        return imageHeight == -1 ? this.height : imageHeight;
    }

    @Override
    protected void init() {
        super.init();
        // 计算居中位置（使用 getImageWidth/Height 保证 resize 后正确）
        this.leftPos = (this.width - getImageWidth()) / 2;
        this.topPos = (this.height - getImageHeight()) / 2;
        widgets.clear();
        modals.clear();
        buildWidgets();
    }

    /** 在这里添加你的组件 */
    protected abstract void buildWidgets();

    /** 添加一个可渲染、可点击的组件 */
    protected void addWidget(PositionedWidget widget) {
        widget.setScreenPos(leftPos, topPos, getImageWidth(), getImageHeight());
        widgets.add(widget);
    }

    /** 添加一个模态对话框 */
    protected void addModal(Modal modal) {
        modal.setScreenPos(leftPos, topPos, getImageWidth(), getImageHeight());
        modals.add(modal);
    }

    /** 关闭所有模态对话框 */
    protected void closeAllModals() {
        for (var m : modals) m.hide();
    }

    /** 添加一个标准 AbstractWidget（如 Button），会自动处理点击和渲染 */
    protected <T extends AbstractWidget> T addWidget(T widget) {
        widget.setPosition(leftPos + widget.getX(), leftPos + widget.getY());
        addRenderableWidget(widget);
        return widget;
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int w = getImageWidth();
        int h = getImageHeight();
        // 1) 绘制纯色背景（如果有）
        if (backgroundColor != -1) {
            graphics.fill(leftPos, topPos, leftPos + w, topPos + h, backgroundColor);
        }
        // 2) 绘制纹理背景（如果有）
        if (BACKGROUND != null) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, w, h, w, h);
        }
        // 3) 绘制所有自定义组件（有模态时屏蔽底层悬停，传 -1 坐标）
        boolean modalActive = false;
        for (var m : modals) if (m.isVisible()) { modalActive = true; break; }
        int wmx = modalActive ? -1 : mouseX;
        int wmy = modalActive ? -1 : mouseY;
        for (var widget : widgets) {
            widget.render(graphics, wmx, wmy, partialTick);
        }
        // 4) 把展开的下拉菜单绘制在最上层（同样屏蔽悬停）
        for (var widget : widgets) {
            renderDropdownsOnTop(widget, graphics, wmx, wmy, partialTick);
        }
        // 5) 绘制模态对话框（在一切之上，使用真实坐标）
        for (var modal : modals) {
            modal.render(graphics, mouseX, mouseY, partialTick);
        }
        // 6) 绘制标准组件（AbstractWidget 等）
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    /** 递归查找并重新绘制所有展开的 DropdownMenu（确保菜单不会被遮挡） */
    private void renderDropdownsOnTop(PositionedWidget w, GuiGraphicsExtractor g, int mx, int my, float pt) {
        if (w instanceof DropdownMenu<?> dm && dm.isExpanded()) {
            dm.render(g, mx, my, pt);
        }
        if (w instanceof Panel panel) {
            for (var child : panel.collectAllChildren()) {
                if (child instanceof DropdownMenu<?> dm && dm.isExpanded()) {
                    dm.render(g, mx, my, pt);
                }
            }
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        // ESC 关闭最上层的模态对话框
        int code = event.key();
        if (code == 256 || code == 27) { // GLFW_KEY_ESCAPE
            for (int i = modals.size() - 1; i >= 0; i--) {
                var m = modals.get(i);
                if (m.isVisible()) {
                    m.hide();
                    return true;
                }
            }
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        // 1) 模态对话框优先（阻挡底层点击）
        for (var modal : modals) {
            if (modal.isVisible() && modal.handleClick(event, doubleClick)) {
                return true;
            }
        }
        // 2) 递归检查所有 widget（含嵌套子组件）
        for (var widget : widgets) {
            if (checkClickRecursive(widget, event, doubleClick)) {
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    /** 递归检查 widget 及其所有嵌套子组件的点击 */
    private boolean checkClickRecursive(PositionedWidget w, MouseButtonEvent event, boolean doubleClick) {
        if (w instanceof DropdownMenu<?> dm && dm.handleClick(event, doubleClick)) {
            return true;
        }
        if (w instanceof CustomButton btn && btn.handleClick(event, doubleClick)) {
            return true;
        }
        if (w instanceof Panel panel) {
            if (panel.handleClick(event, doubleClick)) {
                return true;
            }
            for (var child : panel.collectAllChildren()) {
                if (child instanceof DropdownMenu<?> d && d.handleClick(event, doubleClick)) {
                    return true;
                }
                if (child instanceof CustomButton b && b.handleClick(event, doubleClick)) {
                    return true;
                }
                if (child instanceof Panel p && p.handleClick(event, doubleClick)) {
                    return true;
                }
            }
        }
        return false;
    }
}

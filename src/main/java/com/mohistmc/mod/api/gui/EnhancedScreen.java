package com.mohistmc.mod.api.gui;

import com.mohistmc.mod.api.gui.editor.GuiEditorManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public abstract class EnhancedScreen extends Screen {
    /** 基准分辨率 — 所有组件按此尺寸布局，通过矩阵变换等比缩放适配不同屏幕 */
    public static final int BASE_W = 1280;
    public static final int BASE_H = 720;

    @Nullable
    protected final Identifier BACKGROUND;
    protected final int backgroundColor;

    /** 缩放因子（每帧计算） */
    private float scale = 1.0f;
    /** 画布在屏幕上的偏移（每帧计算） */
    private int canvasOffsetX, canvasOffsetY;
    /** 实际画布逻辑高度（窄屏时扩展填满屏幕，消除上下边距） */
    private int actualImageHeight = BASE_H;

    private final List<PositionedWidget> widgets = new ArrayList<>();
    private final List<Modal> modals = new ArrayList<>();
    /** 当前聚焦的自定义文本输入框（键盘/字符事件分发目标） */
    @Nullable
    private TextInputWidget focusedTextInput;

    protected EnhancedScreen(Component title, int backgroundColor) {
        super(title);
        this.BACKGROUND = null;
        this.backgroundColor = backgroundColor;
    }

    protected EnhancedScreen(Component title, @org.jspecify.annotations.Nullable Identifier background) {
        super(title);
        this.BACKGROUND = background;
        this.backgroundColor = -1;
    }

    protected EnhancedScreen(Component title, @Nullable Identifier background, int backgroundColor) {
        super(title);
        this.BACKGROUND = background;
        this.backgroundColor = backgroundColor;
    }

    protected int getImageWidth() { return BASE_W; }
    protected int getImageHeight() { return actualImageHeight; }

    public float getScale() { return scale; }
    public int getCanvasOffsetX() { return canvasOffsetX; }
    public int getCanvasOffsetY() { return canvasOffsetY; }

    public int toLogicalX(double screenX) { return (int) ((screenX - canvasOffsetX) / scale); }
    public int toLogicalY(double screenY) { return (int) ((screenY - canvasOffsetY) / scale); }
    public float toLogicalXF(double screenX) { return (float) ((screenX - canvasOffsetX) / scale); }
    public float toLogicalYF(double screenY) { return (float) ((screenY - canvasOffsetY) / scale); }

    /** 返回全屏高度（逻辑像素），用于窄屏时让组件填满屏幕 */
    public int getFullHeight() {
        return (int) Math.ceil(this.height / scale);
    }

    /** 返回画布顶部偏移对应的逻辑像素值（负值），用于计算全屏面板的起始Y */
    public int getCanvasOffsetLogical() {
        return -(int) Math.ceil(canvasOffsetY / scale);
    }

    /** 逻辑坐标 → 屏幕坐标 X */
    public int toScreenX(double logicalX) { return (int) (canvasOffsetX + logicalX * scale); }
    /** 逻辑坐标 → 屏幕坐标 Y */
    public int toScreenY(double logicalY) { return (int) (canvasOffsetY + logicalY * scale); }

    @Override
    protected void init() {
        super.init();
        computeTransform();
        widgets.clear();
        modals.clear();
        buildWidgets();
        GuiEditorManager.setScreen(this);
    }

    protected abstract void buildWidgets();

    public void rebuildWidgets() {
        this.clearWidgets();
        this.init();
    }

    protected void addWidget(PositionedWidget widget) {
        int logicalW = (int) Math.ceil((this.width - canvasOffsetX) / scale);
        int logicalH = (int) Math.ceil((this.height - canvasOffsetY) / scale);
        widget.setScreenPos(0, 0, logicalW, logicalH);
        widgets.add(widget);
    }

    protected void addModal(Modal modal) {
        int logicalW = (int) Math.ceil((this.width - canvasOffsetX) / scale);
        int logicalH = (int) Math.ceil((this.height - canvasOffsetY) / scale);
        modal.setScreenPos(0, 0, logicalW, logicalH);
        modals.add(modal);
    }

    /** 以屏幕全尺寸注册一个模态框（供编辑器等外部调用） */
    public void addModalExternal(Modal modal) {
        addModal(modal);
    }

    /** 返回所有根级自定义组件列表（用于编辑器遍历） */
    public List<PositionedWidget> getWidgets() {
        return widgets;
    }

    protected void closeAllModals() {
        for (var m : modals) m.hide();
    }

    // ======== 自定义文本输入框聚焦管理 ========

    public TextInputWidget getFocusedTextInput() {
        return focusedTextInput;
    }

    /** 切换聚焦输入框（旧输入框自动失焦；传 null 表示取消聚焦） */
    public void setFocusedTextInput(@Nullable TextInputWidget input) {
        if (focusedTextInput != null && focusedTextInput != input) {
            focusedTextInput.releaseFocus();
        }
        focusedTextInput = input;
        if (input != null) {
            input.requestFocus();
        }
    }

    /** 将逻辑坐标的 AbstractWidget 转换为屏幕坐标后添加 */
    protected <T extends AbstractWidget> T addWidget(T widget) {
        int sx = (int) (canvasOffsetX + widget.getX() * scale);
        int sy = (int) (canvasOffsetY + widget.getY() * scale);
        widget.setPosition(sx, sy);
        addRenderableWidget(widget);
        return widget;
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        computeTransform();

        boolean modalActive = false;
        for (var m : modals) if (m.isVisible()) { modalActive = true; break; }

        // —— 1) 背景（全屏渲染，不通过缩放矩阵） ——
        int sw = this.width;
        int sh = this.height;
        if (backgroundColor != -1) {
            graphics.fill(0, 0, sw, sh, backgroundColor);
        }
        if (BACKGROUND != null) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, 0, 0, 0, 0, sw, sh, sw, sh);
        }

        // —— 2) 自定义组件（在矩阵内，使用逻辑坐标） ——
        int lmx = modalActive ? -1 : toLogicalX(mouseX);
        int lmy = modalActive ? -1 : toLogicalY(mouseY);

        var pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(canvasOffsetX, canvasOffsetY);
        pose.scale(scale, scale);
        for (var widget : widgets) {
            widget.render(graphics, lmx, lmy, partialTick);
        }
        for (var widget : widgets) {
            renderDropdownsOnTop(widget, graphics, lmx, lmy, partialTick);
        }
        pose.popMatrix();

        // —— 3) NeoForge 标准组件（在矩阵外，使用屏幕坐标） ——
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        // —— 4) 模态对话框（在矩阵内） ——
        int mlmx = toLogicalX(mouseX);
        int mlmy = toLogicalY(mouseY);
        pose.pushMatrix();
        pose.translate(canvasOffsetX, canvasOffsetY);
        pose.scale(scale, scale);
        for (var modal : modals) {
            modal.render(graphics, mlmx, mlmy, partialTick);
        }
        pose.popMatrix();

        // —— 5) GUI 编辑器覆盖层（在矩阵内，编辑模式时渲染） ——
        if (GuiEditorManager.isActive()) {
            pose.pushMatrix();
            pose.translate(canvasOffsetX, canvasOffsetY);
            pose.scale(scale, scale);
            GuiEditorManager.render(graphics, mlmx, mlmy, partialTick);
            pose.popMatrix();
        }
    }

    /** 子类可重写此方法返回 true 使画布靠左对齐（而非居中），适用于 EscGui 等左侧面板 */
    protected boolean isLeftAligned() { return false; }

    private void computeTransform() {
        int sw = this.width;
        int sh = this.height;
        scale = Math.min((float) sw / BASE_W, (float) sh / BASE_H);
        scale = Math.max(0.25f, scale);
        if (isLeftAligned()) {
            canvasOffsetX = 0;
        } else {
            canvasOffsetX = (int) ((sw - BASE_W * scale) / 2);
        }
        // 窄屏时（scale 受宽度限制），扩展画布逻辑高度填满屏幕，消除上下边距
        float scaledH = BASE_H * scale;
        if (scaledH < sh) {
            actualImageHeight = (int) Math.ceil(sh / scale);
            canvasOffsetY = 0;
        } else {
            actualImageHeight = BASE_H;
            canvasOffsetY = (int) ((sh - scaledH) / 2);
        }
        GuiCoord.setTransform(scale, canvasOffsetX, canvasOffsetY);
    }

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
    public void removed() {
        // 关闭屏幕时清理 IME 状态
        if (focusedTextInput != null) {
            focusedTextInput.releaseFocus();
            focusedTextInput = null;
        }
        GuiEditorManager.setScreen(null);
        super.removed();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        // 优先分发到聚焦的文本输入框
        if (focusedTextInput != null && focusedTextInput.consumeKey(event)) {
            return true;
        }
        int code = event.key();
        // 编辑模式快捷键
        if (GuiEditorManager.isActive()) {
            // F8: 关闭编辑模式
            if (code == 0x129) { // GLFW_KEY_F8 = 297
                GuiEditorManager.toggle();
                return true;
            }
            // Ctrl+Shift+S: 另存为（自定义文件名）
            if (code == 0x53 && (event.modifiers() & 0x2) != 0 && (event.modifiers() & 0x1) != 0) {
                GuiEditorManager.startSaveAs();
                return true;
            }
            // Ctrl+S: 保存布局
            if (code == 0x53 && (event.modifiers() & 0x2) != 0) { // GLFW_KEY_S + Ctrl
                GuiEditorManager.saveLayout();
                return true;
            }
            // Esc: 取消选中组件
            if (code == 256 || code == 27) {
                // 不关闭屏幕，只取消选中
                return true;
            }
        }
        // 全局 F8 快捷键（任何模式下切换编辑器）
        if (code == 0x129) { // GLFW_KEY_F8 = 297
            GuiEditorManager.toggle();
            return true;
        }
        if (code == 256 || code == 27) {
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
    public boolean charTyped(CharacterEvent event) {
        if (focusedTextInput != null && focusedTextInput.consumeChar(event)) {
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        int lx = GuiCoord.toLogicalX(event.x());
        int ly = GuiCoord.toLogicalY(event.y());

        // 编辑模式拖拽
        if (GuiEditorManager.isActive()) {
            GuiEditorManager.handleDrag(lx, ly);
            return true;
        }

        for (var modal : modals) {
            if (modal.isVisible()) {
                modal.handleDrag(lx, ly);
                return true;
            }
        }
        for (var widget : widgets) {
            if (dragScrollListRecursive(widget, lx, ly)) return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (GuiEditorManager.isActive()) {
            GuiEditorManager.handleRelease();
        }
        for (var modal : modals) {
            modal.handleRelease();
        }
        for (var widget : widgets) {
            releaseScrollListRecursive(widget);
        }
        return super.mouseReleased(event);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (GuiEditorManager.isActive()) {
            int lx = toLogicalX(mouseX);
            int ly = toLogicalY(mouseY);
            GuiEditorManager.handleMouseMove(lx, ly);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    private boolean dragScrollListRecursive(PositionedWidget w, int mx, int my) {
        if (w instanceof ScrollableWidget scrollable) { scrollable.handleDrag(mx, my); return true; }
        if (w instanceof Panel panel) {
            for (var child : panel.collectAllChildren()) {
                if (child instanceof ScrollableWidget scrollable) { scrollable.handleDrag(mx, my); return true; }
            }
        }
        return false;
    }

    private void releaseScrollListRecursive(PositionedWidget w) {
        if (w instanceof ScrollableWidget scrollable) scrollable.handleRelease();
        if (w instanceof Panel panel) {
            for (var child : panel.collectAllChildren()) {
                if (child instanceof ScrollableWidget scrollable) scrollable.handleRelease();
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        // 编辑模式优先处理
        if (GuiEditorManager.isActive()) {
            if (GuiEditorManager.handleClick(event)) {
                return true;
            }
        }

        // 1) 文本输入框优先：点击即聚焦并定位光标
        TextInputWidget input = findTextInputAt(event);
        if (input != null) {
            setFocusedTextInput(input);
            input.handleClick(event, doubleClick);
            return true;
        }
        // 点击其他区域时取消输入框聚焦
        setFocusedTextInput(null);

        for (var modal : modals) {
            if (modal.isVisible() && modal.handleClick(event, doubleClick)) {
                return true;
            }
        }
        for (var widget : widgets) {
            if (checkClickRecursive(widget, event, doubleClick)) {
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    /** 递归查找点击位置命中的文本输入框（含 Panel 子组件） */
    private TextInputWidget findTextInputAt(MouseButtonEvent event) {
        int lx = GuiCoord.toLogicalX(event.x());
        int ly = GuiCoord.toLogicalY(event.y());
        for (var widget : widgets) {
            var hit = findTextInputRecursive(widget, lx, ly);
            if (hit != null) return hit;
        }
        return null;
    }

    private TextInputWidget findTextInputRecursive(PositionedWidget w, int lx, int ly) {
        if (w instanceof TextInputWidget ti && ti.isMouseOver(lx, ly)) {
            return ti;
        }
        if (w instanceof Panel panel) {
            for (var child : panel.collectAllChildren()) {
                var hit = findTextInputRecursive(child, lx, ly);
                if (hit != null) return hit;
            }
        }
        return null;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int lx = GuiCoord.toLogicalX(mouseX);
        int ly = GuiCoord.toLogicalY(mouseY);

        for (var modal : modals) {
            if (modal.isVisible() && modal.handleScroll(lx, ly, scrollY)) {
                return true;
            }
        }
        for (var modal : modals) {
            if (modal.isVisible()) return true;
        }
        for (var widget : widgets) {
            if (scrollListRecursive(widget, lx, ly, scrollY)) return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private boolean scrollListRecursive(PositionedWidget w, double mx, double my, double delta) {
        if (w instanceof ScrollableWidget scrollable && scrollable.handleScroll(mx, my, delta)) return true;
        if (w instanceof Panel panel) {
            for (var child : panel.collectAllChildren()) {
                if (child instanceof ScrollableWidget scrollable && scrollable.handleScroll(mx, my, delta)) return true;
            }
        }
        return false;
    }

    private boolean checkClickRecursive(PositionedWidget w, MouseButtonEvent event, boolean doubleClick) {
        if (w instanceof ScrollableWidget scrollable && scrollable.handleClick(event, doubleClick)) {
            return true;
        }
        if (w instanceof DropdownMenu<?> dm && dm.handleClick(event, doubleClick)) {
            return true;
        }
        if (w instanceof CustomButton btn && btn.handleClick(event, doubleClick)) {
            return true;
        }
        if (w instanceof IconButton ib && ib.handleClick(event, doubleClick)) {
            return true;
        }
        if (w instanceof Panel panel) {
            if (panel.handleClick(event, doubleClick)) {
                return true;
            }
            for (var child : panel.collectAllChildren()) {
                if (child instanceof ScrollableWidget scrollable && scrollable.handleClick(event, doubleClick)) {
                    return true;
                }
                if (child instanceof DropdownMenu<?> d && d.handleClick(event, doubleClick)) {
                    return true;
                }
                if (child instanceof CustomButton b && b.handleClick(event, doubleClick)) {
                    return true;
                }
                if (child instanceof IconButton ib && ib.handleClick(event, doubleClick)) {
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

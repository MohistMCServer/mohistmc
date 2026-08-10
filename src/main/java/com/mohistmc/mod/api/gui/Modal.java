package com.mohistmc.mod.api.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 模态对话框 — 带有半透遮罩层的居中弹窗，支持标题、内容、自定义按钮、ESC/背景关闭
 *
 * <pre>
 * 用法:
 *   var modal = new Modal(Component.literal("提示"), Component.literal("确定要删除吗？"))
 *       .onConfirm(() -> { ... })
 *       .onCancel(() -> {});
 *   addModal(modal);
 *   modal.show();
 * </pre>
 */
@OnlyIn(Dist.CLIENT)
public class Modal extends PositionedWidget {

    // ======== 内置按钮封装 ========

    /** 对话框按钮 */
    public static class ModalButton {
        public final Component text;
        public final int bgColor;
        public final int hoverColor;
        public final int textColor;
        final Runnable action;

        public ModalButton(Component text, int bgColor, int hoverColor, int textColor, Runnable action) {
            this.text = text;
            this.bgColor = bgColor;
            this.hoverColor = hoverColor;
            this.textColor = textColor;
            this.action = action;
        }
    }

    // ======== 字段 ========

    private final Component title;
    private final Component message;

    // 样式
    protected int backdropColor = 0x80000000;
    private int dialogBgColor = 0xFF2D2D2D;
    private int borderColor = 0xFF888888;
    private int titleColor = 0xFFFFFFFF;
    private int messageColor = 0xFFCCCCCC;
    private int separatorColor = 0xFF555555;

    // 尺寸
    private int dialogWidth = 280;
    private int titleBarHeight = 22;
    private int buttonHeight = 20;
    private int padding = 12;
    private int buttonGap = 8;

    // 按钮
    private final List<ModalButton> buttons = new ArrayList<>();
    private Runnable onClose;

    // 状态
    protected boolean visible = true;
    protected boolean closeOnBackdrop = true;
    private boolean initialized;

    // 引用
    private int closeBtnHovered; // 0=not, 1=hovered

    // ======== 构造 ========

    /** 纯标题+内容，无按钮（需自行 addButton） */
    public Modal(Component title, Component message) {
        super(0, 0, 0, 0); // 由 setScreenPos 重新设定尺寸
        this.title = title;
        this.message = message;
        hide(); // 默认隐藏
    }

    /** 快捷构造：带确定/取消按钮 */
    public Modal(Component title, Component message, Runnable onConfirm, Runnable onCancel) {
        this(title, message);
        addConfirmButton(onConfirm);
        addCancelButton(onCancel);
    }

    // ======== 链式配置 ========

    public Modal setBackdropColor(int color) { this.backdropColor = color; return this; }
    public Modal setDialogBgColor(int color) { this.dialogBgColor = color; return this; }
    public Modal setDialogWidth(int width) { this.dialogWidth = Math.max(140, width); return this; }
    public Modal setTitleColor(int color) { this.titleColor = color; return this; }
    public Modal setMessageColor(int color) { this.messageColor = color; return this; }
    public Modal setBorderColor(int color) { this.borderColor = color; return this; }
    public Modal setSeparatorColor(int color) { this.separatorColor = color; return this; }
    public Modal setCloseOnBackdrop(boolean close) { this.closeOnBackdrop = close; return this; }

    /** 添加自定义按钮（从右往左排列） */
    public Modal addButton(ModalButton btn) {
        buttons.add(btn);
        return this;
    }

    /** 添加确定按钮（默认绿色） */
    public Modal addConfirmButton(Runnable action) {
        buttons.add(new ModalButton(Component.literal("确定"), 0xFF4CAF50, 0xFF66BB6A, 0xFFFFFFFF, action));
        return this;
    }

    /** 添加取消按钮（默认灰色） */
    public Modal addCancelButton(Runnable action) {
        buttons.add(new ModalButton(Component.literal("取消"), 0xFF666666, 0xFF888888, 0xFFFFFFFF, action));
        return this;
    }

    /** 右上角关闭回调 */
    public Modal onClose(Runnable cb) { this.onClose = cb; return this; }

    // ======== 显示控制 ========

    public void show() { visible = true; }
    public void hide() { visible = false; closeBtnHovered = 0; }
    public boolean isVisible() { return visible; }

    // ======== 滚动/拖拽（子类可重写以路由到内部滚动组件） ========

    /** 滚轮事件，返回 true 表示已消费 */
    public boolean handleScroll(double mouseX, double mouseY, double delta) { return false; }

    /** 拖拽事件 */
    public void handleDrag(double mouseX, double mouseY) {}

    /** 释放事件 */
    public void handleRelease() {}

    // ======== 点击 ========

    /** 返回 true 表示消费了此次点击（阻止底层组件响应） */
    boolean handleClick(MouseButtonEvent event, boolean doubleClick) {
        if (!visible) return false;
        int mx = (int) event.x();
        int my = (int) event.y();

        int dx = getDialogX();
        int dy = getDialogY();
        int dh = getDialogHeight();

        // 点击在对话框内部
        if (mx >= dx && mx < dx + dialogWidth && my >= dy && my < dy + dh) {
            // 关闭按钮 ×
            int closeX = dx + dialogWidth - 4 - closeBtnSize();
            int closeY = dy + (titleBarHeight - closeBtnSize()) / 2;
            if (mx >= closeX && mx < closeX + closeBtnSize() && my >= closeY && my < closeY + closeBtnSize()) {
                if (onClose != null) onClose.run();
                hide();
                return true;
            }

            // 底部按钮（整体居中，从左往右排）
            int btnAreaY = dy + dh - padding - buttonHeight;
            int totalW = 0;
            for (var btn : buttons) totalW += font().width(btn.text) + 16;
            totalW += Math.max(0, buttons.size() - 1) * buttonGap;
            int curX = dx + (dialogWidth - totalW) / 2;
            for (var btn : buttons) {
                int bw = font().width(btn.text) + 16;
                if (mx >= curX && mx < curX + bw && my >= btnAreaY && my < btnAreaY + buttonHeight) {
                    if (btn.action != null) btn.action.run();
                    hide();
                    return true;
                }
                curX += bw + buttonGap;
            }

            return true; // 点击对话框内部但不作任何操作也消费事件
        }

        // 点击遮罩层 → 关闭
        if (closeOnBackdrop) {
            hide();
            if (onClose != null) onClose.run();
            return true;
        }
        return false;
    }

    // ======== 渲染 ========

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        int sw = contentWidth;
        int sh = contentHeight;
        int dx = getDialogX();
        int dy = getDialogY();
        int dh = getDialogHeight();

        // 1) 遮罩层
        graphics.fill(screenLeft, screenTop, screenLeft + sw, screenTop + sh, applyAlpha(backdropColor));

        // 2) 对话框背景 + 边框
        int dbg = applyAlpha(dialogBgColor);
        int dbc = applyAlpha(borderColor);
        graphics.fill(dx, dy, dx + dialogWidth, dy + dh, dbg);
        graphics.fill(dx, dy, dx + dialogWidth, dy + 1, dbc);             // 上
        graphics.fill(dx, dy + dh - 1, dx + dialogWidth, dy + dh, dbc);  // 下
        graphics.fill(dx, dy, dx + 1, dy + dh, dbc);                     // 左
        graphics.fill(dx + dialogWidth - 1, dy, dx + dialogWidth, dy + dh, dbc); // 右

        var font = font();

        // 3) 标题（左右居中）
        int titleY = dy + (titleBarHeight - font.lineHeight) / 2;
        graphics.text(font, title, dx + (dialogWidth - font.width(title)) / 2, titleY, applyAlpha(titleColor));

        // 3a) 关闭按钮 ×
        int cs = closeBtnSize();
        int closeX = dx + dialogWidth - 4 - cs;
        int closeY = dy + (titleBarHeight - cs) / 2;
        boolean ch = mouseX >= closeX && mouseX < closeX + cs && mouseY >= closeY && mouseY < closeY + cs;
        closeBtnHovered = ch ? 1 : 0;
        if (ch) {
            graphics.fill(closeX - 1, closeY - 1, closeX + cs + 1, closeY + cs + 1, applyAlpha(0x44FFFFFF));
        }
        graphics.text(font, Component.literal("×"), closeX - 1, closeY - 2, applyAlpha(ch ? 0xFFFF8888 : 0xFFAAAAAA));

        // 4) 分隔线
        int sepY = dy + titleBarHeight;
        graphics.fill(dx + 1, sepY, dx + dialogWidth - 1, sepY + 1, applyAlpha(separatorColor));

        // 5) 内容文字（多行每行左右居中）
        int textY = dy + titleBarHeight + padding;
        var msgLines = font.split(message, dialogWidth - padding * 2);
        for (int i = 0; i < msgLines.size(); i++) {
            var line = msgLines.get(i);
            graphics.text(font, line, dx + (dialogWidth - font.width(line)) / 2,
                    textY + i * font.lineHeight, applyAlpha(messageColor));
        }

        // 6) 底部按钮分隔线
        int btnSepY = dy + dh - padding - buttonHeight - padding / 2;
        graphics.fill(dx + 1, btnSepY, dx + dialogWidth - 1, btnSepY + 1, applyAlpha(separatorColor));

        // 7) 底部按钮（整体居中，从左往右排）
        int btnAreaY = dy + dh - padding - buttonHeight;
        int totalW = 0;
        for (var btn : buttons) totalW += font.width(btn.text) + 16;
        totalW += Math.max(0, buttons.size() - 1) * buttonGap;
        int curX = dx + (dialogWidth - totalW) / 2;
        for (var btn : buttons) {
            int bw = font.width(btn.text) + 16;
            boolean btnHover = mouseX >= curX && mouseX < curX + bw && mouseY >= btnAreaY && mouseY < btnAreaY + buttonHeight;
            int btnBg = applyAlpha(btnHover ? btn.hoverColor : btn.bgColor);
            graphics.fill(curX, btnAreaY, curX + bw, btnAreaY + buttonHeight, btnBg);
            graphics.text(font, btn.text, curX + (bw - font.width(btn.text)) / 2,
                    btnAreaY + (buttonHeight - font.lineHeight) / 2, applyAlpha(btn.textColor));
            curX += bw + buttonGap;
        }
    }

    // ======== 内部计算 ========

    private int getDialogX() {
        return screenLeft + (contentWidth - dialogWidth) / 2;
    }

    private int getDialogY() {
        return screenTop + Math.max(20, (contentHeight - getDialogHeight()) / 2);
    }

    private int getDialogHeight() {
        int msgH = font().wordWrapHeight(message, dialogWidth - padding * 2);
        return titleBarHeight + 1 + padding + msgH + padding / 2 + 1 + padding + buttonHeight + padding;
    }

    private int closeBtnSize() { return 10; }

    private Minecraft mc() { return Minecraft.getInstance(); }
    private net.minecraft.client.gui.Font font() { return mc().font; }

    @Override
    public int applyAlpha(int argb) {
        if (!visible) return 0;
        return super.applyAlpha(argb);
    }
}

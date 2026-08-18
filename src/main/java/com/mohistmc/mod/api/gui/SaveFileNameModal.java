package com.mohistmc.mod.api.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 保存布局文件名输入悬浮窗 — 屏幕居中的模态弹窗，内含文本输入框与 确定/取消 按钮。
 *
 * <p>由 GUI 编辑器在 Ctrl+Shift+S 时弹出，输入文件名后回车或点击「确定」保存，
 * Esc 或点击「取消」关闭。</p>
 */
@OnlyIn(Dist.CLIENT)
public class SaveFileNameModal extends Modal {

    private static final int INPUT_MARGIN_TOP = 14; // 输入框距标题分隔线
    private static final int INPUT_H = 26;
    private static final int BTN_MARGIN_TOP = 16;   // 按钮区距输入框

    private final EnhancedScreen screen;
    private final TextInputWidget input;
    private final Runnable onSave;
    private final Runnable onCancel;

    private final int inputX;
    private final int inputY;
    private final int inputW;

    public SaveFileNameModal(EnhancedScreen screen, Runnable onSave, Runnable onCancel) {
        super(Component.literal("保存布局"), Component.empty());
        this.screen = screen;
        this.onSave = onSave;
        this.onCancel = onCancel;
        setDialogWidth(320);
        setCloseOnBackdrop(false);
        addConfirmButton(this::confirm);
        addCancelButton(this::cancel);

        // 输入框布局（坐标相对对话框左上角）
        this.inputX = padding + 10;
        this.inputY = titleBarHeight + INPUT_MARGIN_TOP;
        this.inputW = dialogWidth - padding * 2 - 20;
        this.input = new TextInputWidget(inputX, inputY, inputW, INPUT_H)
                .setPlaceholder("请输入布局文件名")
                .setMaxLength(32)
                .setFontSize(14);
        this.input.setOnEnter(this::confirm);
        this.input.setOnEscape(this::cancel);
    }

    /** 返回当前输入的文件名（去除首尾空白） */
    public String getEnteredName() {
        return input.getText().trim();
    }

    @Override
    public void show() {
        super.show();
        if (screen != null) {
            screen.setFocusedTextInput(input);
        }
    }

    @Override
    public void hide() {
        super.hide();
        if (screen != null) {
            screen.setFocusedTextInput(null);
        }
    }

    /** 确认：文件名非空则触发保存回调，否则视为取消 */
    private void confirm() {
        if (!getEnteredName().isEmpty()) {
            if (onSave != null) onSave.run();
        } else if (onCancel != null) {
            onCancel.run();
        }
        hide();
    }

    private void cancel() {
        if (onCancel != null) onCancel.run();
        hide();
    }

    /** 对话框总高度 */
    private int dialogHeight() {
        return titleBarHeight + INPUT_MARGIN_TOP + INPUT_H + BTN_MARGIN_TOP + buttonHeight + padding;
    }

    // ======== 渲染 ========

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        int dx = getDialogX();
        int dy = getDialogY();
        int dh = dialogHeight();

        // 遮罩层
        graphics.fill(screenLeft, screenTop, screenLeft + contentWidth, screenTop + contentHeight,
                applyAlpha(backdropColor));

        // 对话框背景 + 边框
        graphics.fill(dx, dy, dx + dialogWidth, dy + dh, applyAlpha(dialogBgColor));
        graphics.fill(dx, dy, dx + dialogWidth, dy + 1, applyAlpha(borderColor));
        graphics.fill(dx, dy + dh - 1, dx + dialogWidth, dy + dh, applyAlpha(borderColor));
        graphics.fill(dx, dy, dx + 1, dy + dh, applyAlpha(borderColor));
        graphics.fill(dx + dialogWidth - 1, dy, dx + dialogWidth, dy + dh, applyAlpha(borderColor));

        var font = font();

        // 标题（居中）
        int titleY = dy + (titleBarHeight - font.lineHeight) / 2;
        graphics.text(font, title, dx + (dialogWidth - font.width(title)) / 2, titleY, applyAlpha(titleColor));

        // 分隔线
        graphics.fill(dx + 1, dy + titleBarHeight, dx + dialogWidth - 1, dy + titleBarHeight + 1,
                applyAlpha(separatorColor));

        // 输入框（定位到对话框内）
        input.setScreenPos(dx, dy, dialogWidth, dh);
        input.render(graphics, mouseX, mouseY, partialTick);

        // 底部按钮（居中排列）
        int btnAreaY = dy + dh - padding - buttonHeight;
        int totalW = 0;
        for (var btn : buttons) totalW += font.width(btn.text) + 16;
        totalW += Math.max(0, buttons.size() - 1) * buttonGap;
        int curX = dx + (dialogWidth - totalW) / 2;
        for (var btn : buttons) {
            int bw = font.width(btn.text) + 16;
            boolean hover = mouseX >= curX && mouseX < curX + bw && mouseY >= btnAreaY && mouseY < btnAreaY + buttonHeight;
            graphics.fill(curX, btnAreaY, curX + bw, btnAreaY + buttonHeight,
                    applyAlpha(hover ? btn.hoverColor : btn.bgColor));
            graphics.text(font, btn.text, curX + (bw - font.width(btn.text)) / 2,
                    btnAreaY + (buttonHeight - font.lineHeight) / 2, applyAlpha(btn.textColor));
            curX += bw + buttonGap;
        }
    }

    // ======== 点击 ========

    @Override
    boolean handleClick(MouseButtonEvent event, boolean doubleClick) {
        if (!visible) return false;
        int mx = logicalX(event);
        int my = logicalY(event);

        int dx = getDialogX();
        int dy = getDialogY();
        int dh = dialogHeight();

        // 同步输入框位置（确保点击命中判断正确）
        input.setScreenPos(dx, dy, dialogWidth, dh);

        // 1) 点击输入框 → 聚焦并定位光标
        if (input.isMouseOver(mx, my)) {
            input.handleClick(event, doubleClick);
            if (screen != null) screen.setFocusedTextInput(input);
            return true;
        }

        // 2) 底部按钮
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

        // 3) 点击对话框内部 → 消费（不关闭）
        if (mx >= dx && mx < dx + dialogWidth && my >= dy && my < dy + dh) {
            return true;
        }

        // 4) 点击遮罩 → 取消
        cancel();
        return true;
    }
}

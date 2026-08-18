package com.mohistmc.mod.api.gui;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.lwjgl.glfw.GLFW;

/**
 * 自定义文本输入框（替代原版 EditBox）
 * <p>基于 {@link PositionedWidget}，在画布缩放矩阵内正常工作，
 * 支持占位符、长度限制、光标定位/移动、退格/删除、Ctrl+A/C/V/X、聚焦高亮。</p>
 *
 * <pre>
 *   var input = new TextInputWidget(x, y, 200, 24)
 *       .setPlaceholder("搜索...").setMaxLength(32)
 *       .setOnChange(text -> { ... });
 *   addWidget(input);
 * </pre>
 */
public class TextInputWidget extends PositionedWidget implements GuiEventListener {

    private String text = "";
    private String placeholder = "";
    private int maxLength = 64;
    /** 光标位置（字符数） */
    private int cursorPos;
    private boolean focused;
    /** 文字缩放（12px 基准） */
    private float fontScale = 1.0f;
    private long lastBlink;
    private boolean cursorVisible = true;
    private Consumer<String> onChange;

    private int bgColor = 0xFF1E1E2E;
    private int borderColor = 0xFF444466;
    private int focusedBorderColor = 0xFF4CAF50;
    private int textColor = 0xFFFFFFFF;
    private int placeholderColor = 0xFF888888;
    /** Enter 键回调（用于模态框确认等） */
    private Runnable onEnter;
    /** Esc 键回调（用于模态框取消等） */
    private Runnable onEscape;

    public TextInputWidget(int relX, int relY, int width, int height) {
        super(relX, relY, width, height);
    }

    // ======== 链式配置 ========

    public TextInputWidget setPlaceholder(String placeholder) {
        this.placeholder = placeholder == null ? "" : placeholder;
        return this;
    }

    public TextInputWidget setMaxLength(int maxLength) {
        this.maxLength = Math.max(1, maxLength);
        return this;
    }

    /** 设置文字缩放（12px 基准字号） */
    public TextInputWidget setFontSize(int size) {
        this.fontScale = Math.max(0.5f, size / 12f);
        return this;
    }

    public TextInputWidget setOnChange(Consumer<String> onChange) {
        this.onChange = onChange;
        return this;
    }

    /** 设置 Enter 键回调（如模态框确认） */
    public TextInputWidget setOnEnter(Runnable onEnter) {
        this.onEnter = onEnter;
        return this;
    }

    /** 设置 Esc 键回调（如模态框取消） */
    public TextInputWidget setOnEscape(Runnable onEscape) {
        this.onEscape = onEscape;
        return this;
    }

    public TextInputWidget setBgColor(int bgColor) {
        this.bgColor = bgColor;
        return this;
    }

    public TextInputWidget setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public TextInputWidget setText(String t) {
        this.text = t == null ? "" : t;
        if (text.length() > maxLength) text = text.substring(0, maxLength);
        this.cursorPos = text.length();
        return this;
    }

    public String getText() {
        return text;
    }

    public boolean isFocused() {
        return focused;
    }

    /** GuiEventListener 接口要求：设置聚焦状态 */
    @Override
    public void setFocused(boolean focused) {
        if (focused) {
            requestFocus();
        } else {
            releaseFocus();
        }
    }

    // ======== 聚焦管理（由 EnhancedScreen 调用） ========

    void requestFocus() {
        focused = true;
        lastBlink = System.currentTimeMillis();
        cursorVisible = true;
        // 通知 TextInputManager 文本输入已激活
        Minecraft.getInstance().onTextInputFocusChange(this, true);
        // 强制启用 IME：
        // startTextInput() 只在 imeRequested 为 true 时才调用 setIMEInputMode(true)，
        // 但 imeRequested 需要等到下一帧 tickDuringTextInput() 才会更新。
        // 而 tickOutsideTextInput() 会在用户切换 IME 时立即关闭它，
        // 导致 IME 永远无法启用。这里直接通过 GLFW 强制启用 IME。
        long window = Minecraft.getInstance().getWindow().handle();
        GLFW.glfwSetInputMode(window, 208903, GLFW.GLFW_TRUE); // 208903 = GLFW_IME_ENABLED
    }

    void releaseFocus() {
        focused = false;
        // 通知 TextInputManager 文本输入已结束，归还 IME 控制权
        Minecraft.getInstance().onTextInputFocusChange(this, false);
    }

    // ======== 点击（EnhancedScreen 事件分发调用） ========

    boolean handleClick(MouseButtonEvent event, boolean doubleClick) {
        if (!isMouseOver(logicalX(event), logicalY(event))) return false;
        requestFocus();
        // 根据点击位置估算光标位置
        var font = Minecraft.getInstance().font;
        int mx = logicalX(event);
        int startX = getAbsoluteX() + 5;
        int best = 0, bestDist = Integer.MAX_VALUE;
        for (int i = 0; i <= text.length(); i++) {
            int w = (int) (font.width(text.substring(0, i)) * fontScale);
            int dist = Math.abs(startX + w - mx);
            if (dist < bestDist) {
                bestDist = dist;
                best = i;
            }
        }
        cursorPos = best;
        return true;
    }

    // ======== 键盘（EnhancedScreen 事件分发调用） ========

    /** 按键处理；返回 true 表示已消费（阻止 Screen 默认处理） */
    boolean consumeKey(KeyEvent event) {
        if (!focused) return false;
        if (event.isSelectAll()) { cursorPos = text.length(); return true; }
        if (event.isCopy()) { copyToClipboard(text); return true; }
        if (event.isCut()) { copyToClipboard(text); text = ""; cursorPos = 0; fireChange(); return true; }
        if (event.isPaste()) { pasteFromClipboard(); return true; }
        if (event.isEscape()) {
            if (onEscape != null) onEscape.run();
            else releaseFocus();
            return true;
        }
        int code = event.key();
        // Enter 确认（优先回调，供模态框使用）
        if (code == GLFW.GLFW_KEY_ENTER || code == GLFW.GLFW_KEY_KP_ENTER) {
            if (onEnter != null) onEnter.run();
            return true;
        }
        if (event.isLeft()) { cursorPos = Math.max(0, cursorPos - 1); return true; }
        if (event.isRight()) { cursorPos = Math.min(text.length(), cursorPos + 1); return true; }
        if (event.isUp()) { cursorPos = 0; return true; }
        if (event.isDown()) { cursorPos = text.length(); return true; }

        if (code == GLFW.GLFW_KEY_BACKSPACE) {
            if (cursorPos > 0) {
                int prev = text.offsetByCodePoints(cursorPos, -1);
                text = text.substring(0, prev) + text.substring(cursorPos);
                cursorPos = prev;
                fireChange();
            }
            return true;
        }
        if (code == GLFW.GLFW_KEY_DELETE) {
            if (cursorPos < text.length()) {
                int next = text.offsetByCodePoints(cursorPos, 1);
                text = text.substring(0, cursorPos) + text.substring(next);
                fireChange();
            }
            return true;
        }
        if (code == GLFW.GLFW_KEY_HOME) { cursorPos = 0; return true; }
        if (code == GLFW.GLFW_KEY_END) { cursorPos = text.length(); return true; }

        // —— 以下按键放行（return false），避免破坏输入法切换/系统快捷键 ——
        // 修饰键本身（Shift/Ctrl/Alt/Win 单独按下，如 Shift 切换输入法中英文）
        if (code == GLFW.GLFW_KEY_LEFT_SHIFT || code == GLFW.GLFW_KEY_RIGHT_SHIFT
                || code == GLFW.GLFW_KEY_LEFT_CONTROL || code == GLFW.GLFW_KEY_RIGHT_CONTROL
                || code == GLFW.GLFW_KEY_LEFT_ALT || code == GLFW.GLFW_KEY_RIGHT_ALT
                || code == GLFW.GLFW_KEY_LEFT_SUPER || code == GLFW.GLFW_KEY_RIGHT_SUPER) {
            return false;
        }
        // 未识别的 Ctrl/Alt/Win 组合键（如输入法切换 Ctrl+Space）
        if ((event.modifiers() & (GLFW.GLFW_MOD_CONTROL | GLFW.GLFW_MOD_ALT | GLFW.GLFW_MOD_SUPER)) != 0) {
            return false;
        }
        // 其余按键（字母/数字等）聚焦时消费，字符由 charTyped 输入
        return true;
    }

    /** 字符输入；返回 true 表示已消费 */
    boolean consumeChar(CharacterEvent event) {
        if (!focused) return false;
        String s = event.codepointAsString();
        if (s.isEmpty() || !event.isAllowedChatCharacter()) return false;
        if (text.length() >= maxLength || text.length() + s.length() > maxLength) return true;
        text = text.substring(0, cursorPos) + s + text.substring(cursorPos);
        cursorPos += s.length();
        fireChange();
        return true;
    }

    // ======== 内部 ========

    private void fireChange() {
        if (onChange != null) onChange.accept(text);
    }

    private void copyToClipboard(String value) {
        Minecraft.getInstance().keyboardHandler.setClipboard(value);
    }

    private void pasteFromClipboard() {
        String clip = Minecraft.getInstance().keyboardHandler.getClipboard();
        if (clip == null || clip.isEmpty()) return;
        int room = maxLength - text.length();
        if (room <= 0) return;
        String insert = clip.length() > room ? clip.substring(0, room) : clip;
        text = text.substring(0, cursorPos) + insert + text.substring(cursorPos);
        cursorPos += insert.length();
        fireChange();
    }

    @Override
    public void render(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        int x = getAbsoluteX();
        int y = getAbsoluteY();

        // 背景 + 边框（聚焦时边框高亮）
        g.fill(x, y, x + width, y + height, applyAlpha(bgColor));
        int bc = focused ? focusedBorderColor : borderColor;
        g.fill(x, y, x + width, y + 1, bc);
        g.fill(x, y + height - 1, x + width, y + height, bc);
        g.fill(x, y, x + 1, y + height, bc);
        g.fill(x + width - 1, y, x + width, y + height, bc);

        var font = Minecraft.getInstance().font;
        int ty = y + (height - font.lineHeight) / 2;

        if (text.isEmpty()) {
            if (!placeholder.isEmpty()) {
                g.text(font, placeholder, x + 5, ty, applyAlpha(placeholderColor));
            }
        } else {
            // 文字绘制（裁剪到输入框内部，防止过长溢出）
            g.enableScissor(x + 1, y + 1, x + width - 1, y + height - 1);
            var pose = g.pose();
            pose.pushMatrix();
            pose.translate(x + 5, ty);
            pose.scale(fontScale, fontScale);
            pose.translate(-(x + 5), -ty);
            g.text(font, text, x + 5, ty, applyAlpha(textColor));
            pose.popMatrix();
            g.disableScissor();
        }

        // 光标（闪烁）
        if (focused) {
            long now = System.currentTimeMillis();
            if (now - lastBlink > 500) {
                lastBlink = now;
                cursorVisible = !cursorVisible;
            }
            if (cursorVisible) {
                String prefix = text.substring(0, Math.min(cursorPos, text.length()));
                int cx = x + 5 + (int) (font.width(prefix) * fontScale);
                g.fill(cx, y + 2, cx + 1, y + height - 2, 0xFFFFFFFF);
            }
        }
    }
}

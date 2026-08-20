package com.mohistmc.mod.module.mail.client.gui;

import com.mohistmc.mod.api.gui.CustomButton;
import com.mohistmc.mod.api.gui.EnhancedScreen;
import com.mohistmc.mod.api.gui.PositionedWidget;
import com.mohistmc.mod.api.gui.SimpleLabel;
import com.mohistmc.mod.module.mail.common.network.payload.SendBroadcastPayload;
import com.mohistmc.mod.module.mail.common.network.payload.SendMailPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * 发信附件选择界面（单发 /mail send 或群发 /mail sendall 时由服务端打开）：
 * 顶部显示收件人（群发显示「全部玩家」）、消息、可选自定义发送者输入框（空 = 玩家自己，
 * 供 NPC/系统名义发信），中部背包 9×4 网格点选物品（整堆 toggle、选中高亮、悬停 tooltip），
 * 底部已选统计 + 清除 / 发送 / 取消。
 * <p>点击「发送」将消息 + 发件人 + 附件槽位列表发给服务端（服务端校验权限与槽位后扣减背包），
 * 发送后直接关闭界面。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public class MailComposeScreen extends EnhancedScreen {

    private static final int HEADER_H = 70; // 标题 + 收件人 + 消息 + 发送者
    private static final int FOOTER_H = 34; // 已选统计 + 按钮行

    /** 单发时的收件人（群发模式为 null） */
    private final String recipientName;
    private final String message;
    private ComposeGrid grid;
    private SimpleLabel selectedLabel;
    private EditBox senderInput;

    /** 单发模式 */
    public MailComposeScreen(String recipientName, String message) {
        super(Component.translatable("gui.mohistmc.mail.compose.title"), 0xE0101010);
        this.recipientName = recipientName;
        this.message = message;
    }

    /** 群发模式（收件人 = 全部玩家，含离线） */
    public MailComposeScreen(String message) {
        super(Component.translatable("gui.mohistmc.mail.compose.title"), 0xE0101010);
        this.recipientName = null;
        this.message = message;
    }

    @Override
    protected void buildWidgets() {
        int sw = getImageWidth();
        int sh = getImageHeight();
        int guiW = Math.min(300, sw - 16);
        // 槽尺寸随窗口宽度自适应（窄窗口缩小槽，保证网格不超出内容区）
        int slot = Math.max(16, Math.min(22, (guiW - 16) / 9));
        int gridW = 9 * slot + 8;
        int gridH = 4 * slot + 8;
        int guiH = Math.min(HEADER_H + gridH + FOOTER_H + 16, sh - 16);
        int left = (sw - guiW) / 2;
        int top = Math.max(8, (sh - guiH) / 2);

        // —— 整体背景（深色底 + 1px 描边，最先添加画在最底层）——
        addWidget(new Backdrop(left, top, guiW, guiH));

        // —— 顶部：标题 / 收件人 / 消息 / 发送者 ——
        addWidget(new SimpleLabel(left + 4, top + 4,
                Component.translatable("gui.mohistmc.mail.compose.title"), 0xFFFFFFFF).setTextScale(1.1f));
        addWidget(new SimpleLabel(left + 4, top + 22,
                recipientName == null
                        ? Component.translatable("gui.mohistmc.mail.compose.broadcast")
                        : Component.translatable("gui.mohistmc.mail.compose.recipient", recipientName),
                0xFFCCCCCC));
        String msg = Minecraft.getInstance().font.plainSubstrByWidth(message, guiW - 20);
        addWidget(new SimpleLabel(left + 4, top + 36,
                Component.translatable("gui.mohistmc.mail.compose.message", msg), 0xFF888888));

        // 自定义发送者（可选）：空 = 玩家自己的名字，供 NPC/系统名义发信（输入框紧贴文字）
        Component senderLabel = Component.translatable("gui.mohistmc.mail.compose.sender");
        int senderLabelW = Minecraft.getInstance().font.width(senderLabel);
        addWidget(new SimpleLabel(left + 4, top + 52, senderLabel, 0xFF888888));
        senderInput = new EditBox(Minecraft.getInstance().font, left + 8 + senderLabelW, top + 49,
                120, 16, Component.literal(""));
        senderInput.setMaxLength(32);
        senderInput.setValue("");
        addWidget(senderInput);

        // —— 中部：背包网格（居中，钳制在内容区内，不越左右缘） ——
        int gridX = Math.max(left + 4, Math.min(left + (guiW - gridW) / 2, left + guiW - 4 - gridW));
        int gridY = top + HEADER_H;
        grid = new ComposeGrid(gridX, gridY, slot)
                .setOnChange(this::updateSelectedLabel);
        addWidget(grid);

        // —— 底部：已选统计 + 按钮 ——
        int footerY = top + guiH - FOOTER_H;
        selectedLabel = new SimpleLabel(left + 4, footerY + 2,
                Component.translatable("gui.mohistmc.mail.compose.selected", 0), 0xFF888888);
        addWidget(selectedLabel);

        addWidget(new CustomButton(left + 4, footerY + 14, 44, 18,
                Component.translatable("gui.mohistmc.mail.compose.clear"), 0xFF333344)
                .setTextColor(0xFFFFFFFF).setBorderRadius(4)
                .onClick(() -> grid.clear()));
        addWidget(new CustomButton(left + 52, footerY + 14, 60, 18,
                Component.translatable("gui.mohistmc.mail.compose.send"), 0xFF4CAF50)
                .setTextColor(0xFFFFFFFF).setBorderRadius(4)
                .onClick(this::sendMail));
        addWidget(new CustomButton(left + guiW - 64, footerY + 14, 60, 18,
                Component.translatable("gui.mohistmc.mail.compose.cancel"), 0xFF333344)
                .setTextColor(0xFFFFFFFF).setBorderRadius(4)
                .onClick(this::onClose));
    }

    /** 网格点击优先于其他组件分发 */
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) {
            return true;
        }
        return grid != null && grid.handleClick((int) event.x(), (int) event.y());
    }

    private void updateSelectedLabel() {
        if (selectedLabel != null) {
            selectedLabel.setText(Component.translatable(
                    "gui.mohistmc.mail.compose.selected", grid.getSelections().size()));
        }
    }

    /** 发送（附件可为空 = 纯文本），随后关闭界面 */
    private void sendMail() {
        String senderName = senderInput.getValue().trim();
        if (recipientName == null) {
            ClientPacketDistributor.sendToServer(new SendBroadcastPayload(message, senderName, grid.getSelections()));
        } else {
            ClientPacketDistributor.sendToServer(new SendMailPayload(recipientName, message, senderName, grid.getSelections()));
        }
        onClose();
    }

    /** 整体背景：深色底 + 1px 描边（与详情面板一致观感） */
    private static class Backdrop extends PositionedWidget {
        Backdrop(int relX, int relY, int width, int height) {
            super(relX, relY, width, height);
        }

        @Override
        public void render(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
            int x = getAbsoluteX();
            int y = getAbsoluteY();
            g.fill(x, y, x + width, y + height, 0xCC15151C);
            g.fill(x, y, x + width, y + 1, 0xFF44445A);
            g.fill(x, y + height - 1, x + width, y + height, 0xFF44445A);
            g.fill(x, y, x + 1, y + height, 0xFF44445A);
            g.fill(x + width - 1, y, x + width, y + height, 0xFF44445A);
        }
    }
}

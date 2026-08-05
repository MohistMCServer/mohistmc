package com.mohistmc.mod.module.mail.client.gui;

import com.mohistmc.mod.api.gui.CustomButton;
import com.mohistmc.mod.api.gui.EnhancedScreen;
import com.mohistmc.mod.api.gui.Modal;
import com.mohistmc.mod.api.gui.ScrollList;
import com.mohistmc.mod.api.gui.SimpleLabel;
import com.mohistmc.mod.module.mail.common.MailEntry;
import com.mohistmc.mod.module.mail.common.network.payload.ClaimAllMailPayload;
import com.mohistmc.mod.module.mail.common.network.payload.ClaimMailPayload;
import com.mohistmc.mod.module.mail.common.network.payload.ClearReadPayload;
import com.mohistmc.mod.module.mail.common.network.payload.MailboxSyncPayload;
import com.mohistmc.mod.module.mail.common.network.payload.MarkReadPayload;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * 邮箱全屏界面（由服务端 OpenMailboxPayload 打开）
 * <p>布局：标题 + 统计（顶部）；左侧标题列表 ScrollList（MailCard，选中高亮，窄列）；
 * 右侧详情 DetailPanel（发送者 + 时间 + 正文（可滚动）+ 面板内部底部附件区，附件区同行
 * 靠右「领取」按钮）；最底行操作（领取所有 / 清空已读（确认 Modal）/ 关闭）。
 * <p>所有服务端操作结果经 MailboxSyncPayload 回传后重建列表并弹提示，选中邮件按 id 恢复。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
@OnlyIn(Dist.CLIENT)
public class MailScreen extends EnhancedScreen {

    /** 详情面板底部附件区高度（与 DetailPanel.ATTACH_H 保持一致） */
    private static final int ATTACH_H = 30;
    /** 底部操作行高度 */
    private static final int BTN_ROW_H = 24;
    /** 附件区同行「领取」按钮宽度 */
    private static final int CLAIM_BTN_W = 56;

    private List<MailEntry> mails;
    private ScrollList list;
    private SimpleLabel countLabel;
    private SimpleLabel emptyLabel;
    private DetailPanel detailPanel;
    private AttachmentScrollRow attachmentRow;
    private CustomButton claimButton;
    private CustomButton claimAllButton;
    /** 当前选中的邮件（null = 未选中） */
    private MailEntry selected;
    /** 当前弹窗（新建前先隐藏旧的，避免 modals 无限增长） */
    private Modal currentModal;

    public MailScreen(List<MailEntry> mails) {
        super(Component.translatable("gui.mohistmc.mail.title"), 0xE0101010);
        this.mails = mails;
    }

    @Override
    protected void buildWidgets() {
        int sw = getImageWidth();
        int sh = getImageHeight();
        // 响应式内容区宽度（同 ShopScreen）：目标为窗口 65%，上限 640，绝不超出窗口
        int guiW = Math.min(Math.max(sw * 65 / 100, 320), Math.min(640, sw - 16));
        int guiH = sh - 20;
        int left = (sw - guiW) / 2;
        int top = Math.max(8, (sh - guiH) / 2);
        // 左侧窄列表（标题列）+ 右侧详情；窄窗口时列表缩到 80，给详情留足空间
        int listW = Math.max(80, Math.min(150, guiW * 25 / 100));
        int detailX = left + listW + 8;
        int detailW = guiW - listW - 8;
        // 底部仅操作行
        int bottomH = BTN_ROW_H;

        // —— 顶部：标题 + 统计 ——
        addWidget(new SimpleLabel(left + 4, top + 4,
                Component.translatable("gui.mohistmc.mail.title"), 0xFFFFFFFF).setTextScale(1.2f));
        countLabel = new SimpleLabel(left + guiW - 90, top + 8,
                Component.translatable("gui.mohistmc.mail.count", 0), 0xFF888888);
        addWidget(countLabel);

        // —— 左侧：邮件标题列表 ——
        int listTop = top + 24;
        int bodyH = guiH - (listTop - top) - bottomH - 6;
        list = new ScrollList(left, listTop, listW, bodyH, 0x66000000);
        list.setContentPadding(2); // 顶部/底部固定间距，与卡片间隔一致
        addWidget(list);

        // 空状态提示（覆盖在列表上方，mails 为空时显示）
        emptyLabel = new SimpleLabel(left + (listW - 60) / 2, listTop + bodyH / 2,
                Component.translatable("gui.mohistmc.mail.empty"), 0xFF888888);
        addWidget(emptyLabel);

        // —— 右侧：详情面板（附件区在面板内部底部）——
        detailPanel = new DetailPanel(detailX, listTop, detailW, bodyH);
        addWidget(detailPanel);
        // 附件横向滚动条：盖在面板底部附件区（宽 = 面板宽 - 领取按钮位）；
        // 滚轮/滚动条拖拽由 ScrollableWidget 基类自动接入，无需任何手写事件处理
        attachmentRow = new AttachmentScrollRow(detailX, listTop + bodyH - ATTACH_H,
                detailW - CLAIM_BTN_W - 4, ATTACH_H);
        addWidget(attachmentRow);
        // 「领取」按钮：详情面板底部附件区同行、靠右（距面板右缘留 4px；
        // 面板极窄时左缘钳制在面板内，避免按钮溢出内容区）
        int claimX = Math.max(detailX + 2, detailX + detailW - CLAIM_BTN_W - 4);
        claimButton = new CustomButton(claimX,
                listTop + bodyH - ATTACH_H + (ATTACH_H - 20) / 2,
                CLAIM_BTN_W, 20, Component.translatable("gui.mohistmc.mail.claim"), 0xFF4CAF50)
                .setTextColor(0xFFFFFFFF).setBorderRadius(4)
                .onClick(this::claimSelected);
        addWidget(claimButton);

        // —— 最底行：操作 ——
        int btnRowY = top + guiH - bottomH;
        // 「领取所有 / 清空已读」均分左侧标题列表区宽度，与之对齐
        int opBtnW = (listW - 6) / 2;
        claimAllButton = new CustomButton(left, btnRowY, opBtnW, 20,
                Component.translatable("gui.mohistmc.mail.claim_all"), 0xFF4CAF50)
                .setTextColor(0xFFFFFFFF).setBorderRadius(4)
                .onClick(() -> ClientPacketDistributor.sendToServer(ClaimAllMailPayload.INSTANCE));
        claimAllButton.setEnabled(false); // 无可领时禁用，刷新列表时按实际状态更新
        addWidget(claimAllButton);
        addWidget(new CustomButton(left + opBtnW + 6, btnRowY, opBtnW, 20,
                Component.translatable("gui.mohistmc.mail.clear_read"), 0xFFC0392B)
                .setTextColor(0xFFFFFFFF).setBorderRadius(4)
                .onClick(this::confirmClearRead));
        addWidget(new CustomButton(left + guiW - 64, btnRowY, 60, 20,
                Component.translatable("gui.mohistmc.mail.close"), 0xFF333344)
                .setTextColor(0xFFFFFFFF).setBorderRadius(4)
                .onClick(this::onClose)); // Screen.onClose：关闭并返回游戏

        refreshList();
    }

    /** 重建列表与统计（打开时与 MailboxSyncPayload 回传后调用），并恢复选中 */
    private void refreshList() {
        list.clearItems();
        for (MailEntry mail : mails) {
            list.addItem(new MailCard(mail, this::selectMail));
        }
        countLabel.setText(Component.translatable("gui.mohistmc.mail.count", mails.size()));
        // 注意：SimpleLabel 不支持 null 文本，空串不渲染任何内容
        emptyLabel.setText(mails.isEmpty() ? Component.translatable("gui.mohistmc.mail.empty") : Component.literal(""));

        // 选中按 id 恢复（领取/清空后邮件可能被移除）
        MailEntry found = selected == null ? null
                : mails.stream().filter(m -> m.getId() == selected.getId()).findFirst().orElse(null);
        if (found != null) {
            selected = found;
            for (var item : list.getItems()) {
                if (item instanceof MailCard card) {
                    card.setSelected(card.getMail().getId() == found.getId());
                }
            }
        } else {
            selected = null;
        }
        detailPanel.setMail(selected);
        attachmentRow.setMail(selected);
        updateClaimButton();
        updateClaimAllButton();
    }

    /** 是否存在至少一封可领取（未领取且有附件）的邮件 */
    private boolean hasClaimable() {
        for (MailEntry mail : mails) {
            if (!mail.isClaimed() && !mail.getAttachments().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /** 刷新「领取所有」按钮可用状态（无可领时禁用） */
    private void updateClaimAllButton() {
        claimAllButton.setEnabled(hasClaimable());
    }

    // ======== 选中与领取 ========

    /** 点击列表项：切换选中（更新列表高亮 + 右侧详情与领取按钮）；未读邮件查看即标记已读 */
    private void selectMail(MailEntry mail) {
        selected = mail;
        if (!mail.isRead()) {
            // 本地立即置读（列表标识随渲染刷新），并通知服务端持久化
            mail.setRead(true);
            ClientPacketDistributor.sendToServer(new MarkReadPayload(mail.getId()));
        }
        for (var item : list.getItems()) {
            if (item instanceof MailCard card) {
                card.setSelected(card.getMail().getId() == mail.getId());
            }
        }
        detailPanel.setMail(mail);
        attachmentRow.setMail(mail);
        updateClaimButton();
    }

    /**
     * 底部「领取」按钮状态：仅可领取时显示绿色按钮；
     * 无附件（纯文本邮件）或已领取时隐藏——已领取由附件图标上的对勾标识。
     */
    private void updateClaimButton() {
        boolean can = selected != null && !selected.isClaimed() && !selected.getAttachments().isEmpty();
        claimButton.setVisible(can);
        claimButton.setEnabled(can);
        claimButton.setText(Component.translatable("gui.mohistmc.mail.claim"));
        claimButton.setNormalColor(0xFF4CAF50);
    }

    /** 领取按钮：发单封领取请求（服务端校验归属与背包） */
    private void claimSelected() {
        if (selected != null) {
            ClientPacketDistributor.sendToServer(new ClaimMailPayload(selected.getId()));
        }
    }

    // ======== 清空已读（确认后发请求） ========

    private void confirmClearRead() {
        var modal = new Modal(
                Component.translatable("gui.mohistmc.mail.confirm_clear_title"),
                Component.translatable("gui.mohistmc.mail.confirm_clear_message"))
                .setDialogWidth(140);
        modal.addConfirmButton(() -> ClientPacketDistributor.sendToServer(ClearReadPayload.INSTANCE));
        modal.addCancelButton(() -> {});
        showModal(modal);
    }

    // ======== 详情正文滚轮（附件区滚轮/拖拽由 AttachmentScrollRow 基类自动处理） ========

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (detailPanel != null && detailPanel.handleScroll(mouseX, mouseY, scrollY)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    // ======== 服务端回包 ========

    /** MailboxSyncPayload：重建列表 + 结果提示（成功绿色标题，失败红字原因） */
    public void handleMailboxSync(MailboxSyncPayload payload) {
        this.mails = payload.mails();
        refreshList();
        if (payload.success()) {
            // 组合结果：已领取 N 封 / 跳过 M 封 / 清空成功
            StringBuilder sb = new StringBuilder();
            if (payload.claimed() > 0) {
                sb.append(Component.translatable("gui.mohistmc.mail.result.claimed", payload.claimed()).getString());
            }
            if (payload.skipped() > 0) {
                if (!sb.isEmpty()) sb.append("\n");
                sb.append(Component.translatable("gui.mohistmc.mail.result.skipped", payload.skipped()).getString());
            }
            Component msg = sb.isEmpty()
                    ? Component.translatable(payload.message())
                    : Component.literal(sb.toString());
            var modal = new Modal(Component.translatable("gui.mohistmc.mail.success"), msg).setDialogWidth(140);
            modal.addConfirmButton(() -> {});
            showModal(modal);
        } else {
            var modal = new Modal(
                    Component.translatable("gui.mohistmc.mail.fail"),
                    Component.translatable(payload.message()))
                    .setDialogWidth(140)
                    .setMessageColor(0xFFFF5555); // 失败原因红色提示
            modal.addConfirmButton(() -> {});
            showModal(modal);
        }
    }

    private void showModal(Modal modal) {
        if (currentModal != null) {
            currentModal.hide();
        }
        currentModal = modal;
        addModal(modal);
        modal.show();
    }
}

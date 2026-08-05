package com.mohistmc.mod.module.mail.common;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * 单封邮件：发送者名 + 时间戳 + 正文 + 可选物品附件 + 已读/已领取状态。
 * <p>纯文本邮件创建时即视为已领取（无可领附件）；带附件邮件领取后仅置已领取，
 * 附件保留供界面展示（防重复领取由服务端 claimed 校验兜底）。
 * id 全邮箱唯一（由 {@link MailboxData} 的全局 nextId 分配），领取时按 id 定位防跨玩家。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public class MailEntry {

    private final long id;
    private final String senderName;
    private final long timestamp;
    private final String text;
    private List<ItemStack> attachments;
    private boolean read;
    private boolean claimed;

    public MailEntry(long id, String senderName, long timestamp, String text,
                     List<ItemStack> attachments, boolean read, boolean claimed) {
        this.id = id;
        this.senderName = senderName;
        this.timestamp = timestamp;
        this.text = text;
        this.attachments = attachments;
        this.read = read;
        this.claimed = claimed;
    }

    public long getId() {
        return id;
    }

    public String getSenderName() {
        return senderName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    public List<ItemStack> getAttachments() {
        return attachments;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }

    // ======== 网络编解码（common 共用；附件用已验证的 OPTIONAL_LIST_STREAM_CODEC） ========

    public static final StreamCodec<RegistryFriendlyByteBuf, MailEntry> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, MailEntry::getId,
            ByteBufCodecs.STRING_UTF8, MailEntry::getSenderName,
            ByteBufCodecs.VAR_LONG, MailEntry::getTimestamp,
            ByteBufCodecs.STRING_UTF8, MailEntry::getText,
            ItemStack.OPTIONAL_LIST_STREAM_CODEC, MailEntry::getAttachments,
            ByteBufCodecs.BOOL, MailEntry::isRead,
            ByteBufCodecs.BOOL, MailEntry::isClaimed,
            MailEntry::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, List<MailEntry>> LIST_STREAM_CODEC =
            STREAM_CODEC.apply(ByteBufCodecs.collection(NonNullList::createWithCapacity));
}

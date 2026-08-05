package com.mohistmc.mod.module.mail.common.network.payload;

import com.mohistmc.mod.MohistMC;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端 → 服务端：发信（附件选择界面确认后发送）。
 * 服务端重新解析收件人、校验权限与附件槽位（防伪造/防复制），校验通过后扣减背包再发信。
 *
 * @param recipientName 收件人玩家名
 * @param message       邮件正文
 * @param senderName    自定义发送者名（空 = 玩家自己的名字，供 NPC/系统名义发信）
 * @param attachments   附件选择列表（槽位 + 整堆副本，可为空 = 纯文本邮件）
 * @author Mgazul
 * @date 2026/8/5
 */
public record SendMailPayload(String recipientName, String message, String senderName,
                              List<AttachmentSelection> attachments) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MohistMC.MODID, "send_mail");
    public static final Type<SendMailPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SendMailPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SendMailPayload::recipientName,
            ByteBufCodecs.STRING_UTF8, SendMailPayload::message,
            ByteBufCodecs.STRING_UTF8, SendMailPayload::senderName,
            AttachmentSelection.LIST_STREAM_CODEC, SendMailPayload::attachments,
            SendMailPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package com.mohistmc.mod.module.mail.common.network.payload;

import com.mohistmc.mod.MohistMC;
import com.mohistmc.mod.module.mail.common.MailEntry;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端 → 客户端：打开邮箱（附完整邮件列表，不改已读状态；
 * 用户选中查看某封时由客户端发 MarkReadPayload 逐封标记）
 *
 * @param mails 当前玩家的全部邮件（新→旧排序）
 * @author Mgazul
 * @date 2026/8/5
 */
public record OpenMailboxPayload(List<MailEntry> mails) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MohistMC.MODID, "open_mailbox");
    public static final Type<OpenMailboxPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenMailboxPayload> STREAM_CODEC = StreamCodec.composite(
            MailEntry.LIST_STREAM_CODEC, OpenMailboxPayload::mails,
            OpenMailboxPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

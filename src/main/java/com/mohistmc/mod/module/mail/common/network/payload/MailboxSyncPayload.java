package com.mohistmc.mod.module.mail.common.network.payload;

import com.mohistmc.mod.MohistMC;
import com.mohistmc.mod.module.mail.common.MailEntry;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端 → 客户端：邮箱操作结果（领取/领取所有/清空已读），附全量列表供客户端刷新。
 * <p>success=false 时 message 为失败文案 key（如背包满）；success=true 时 claimed/skipped
 * 供客户端组结果提示（如"已领取 %s 封"、"%s 封因背包空间不足未领取"）。
 *
 * @param success 操作是否成功
 * @param message 结果/失败文案 key
 * @param claimed 成功领取的封数
 * @param skipped 因背包空间不足跳过的封数
 * @param mails   操作后全量邮件列表（新→旧）
 * @author Mgazul
 * @date 2026/8/5
 */
public record MailboxSyncPayload(boolean success, String message, int claimed, int skipped,
                                 List<MailEntry> mails) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MohistMC.MODID, "mailbox_sync");
    public static final Type<MailboxSyncPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, MailboxSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, MailboxSyncPayload::success,
            ByteBufCodecs.STRING_UTF8, MailboxSyncPayload::message,
            ByteBufCodecs.INT, MailboxSyncPayload::claimed,
            ByteBufCodecs.INT, MailboxSyncPayload::skipped,
            MailEntry.LIST_STREAM_CODEC, MailboxSyncPayload::mails,
            MailboxSyncPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

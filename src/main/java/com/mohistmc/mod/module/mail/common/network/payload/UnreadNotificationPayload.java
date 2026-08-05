package com.mohistmc.mod.module.mail.common.network.payload;

import com.mohistmc.mod.MohistMC;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端 → 客户端：登录未读邮件提醒（数量 > 0 时才发送，客户端热键栏提示）
 *
 * @param unread 未读邮件数
 * @author Mgazul
 * @date 2026/8/5
 */
public record UnreadNotificationPayload(int unread) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MohistMC.MODID, "unread_mail_notify");
    public static final Type<UnreadNotificationPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, UnreadNotificationPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, UnreadNotificationPayload::unread,
            UnreadNotificationPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

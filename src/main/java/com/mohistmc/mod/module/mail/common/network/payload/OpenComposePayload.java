package com.mohistmc.mod.module.mail.common.network.payload;

import com.mohistmc.mod.MohistMC;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端 → 客户端：打开发信附件选择界面（/mail send 空手触发）。
 *
 * @param recipientName 收件人玩家名（发送时重新解析）
 * @param message       邮件正文
 * @author Mgazul
 * @date 2026/8/5
 */
public record OpenComposePayload(String recipientName, String message) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MohistMC.MODID, "open_compose");
    public static final Type<OpenComposePayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenComposePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, OpenComposePayload::recipientName,
            ByteBufCodecs.STRING_UTF8, OpenComposePayload::message,
            OpenComposePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

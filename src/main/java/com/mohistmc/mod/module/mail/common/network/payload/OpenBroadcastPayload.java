package com.mohistmc.mod.module.mail.common.network.payload;

import com.mohistmc.mod.MohistMC;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端 → 客户端：打开发信附件选择界面（群发模式，/mail sendall 由玩家触发时打开）。
 * 可自定义发送者名、从背包多选物品（不选即纯文本），确认后经 SendBroadcastPayload 发送。
 *
 * @param message 邮件正文
 * @author Mgazul
 * @date 2026/8/5
 */
public record OpenBroadcastPayload(String message) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MohistMC.MODID, "open_broadcast");
    public static final Type<OpenBroadcastPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenBroadcastPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, OpenBroadcastPayload::message,
            OpenBroadcastPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

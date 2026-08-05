package com.mohistmc.mod.module.mail.common.network.payload;

import com.mohistmc.mod.MohistMC;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端 → 服务端：请求打开邮箱（ESC 菜单入口，复用 /mail 的打开逻辑）。
 * 服务端校验身份后标记全部已读，回 OpenMailboxPayload（含完整邮件列表）。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public record OpenMailboxRequestPayload() implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MohistMC.MODID, "open_mailbox_request");
    public static final Type<OpenMailboxRequestPayload> TYPE = new Type<>(ID);
    public static final OpenMailboxRequestPayload INSTANCE = new OpenMailboxRequestPayload();
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenMailboxRequestPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

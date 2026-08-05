package com.mohistmc.mod.module.mail.common.network.payload;

import com.mohistmc.mod.MohistMC;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端 → 服务端：标记单封邮件已读（用户在邮箱界面选中查看该封时发送）。
 * 打开邮箱本身不再改变已读状态，只有真正看过才已读。
 *
 * @param mailId 邮件 id（服务端按 id 在请求者自己的桶内定位）
 * @author Mgazul
 * @date 2026/8/5
 */
public record MarkReadPayload(long mailId) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MohistMC.MODID, "mark_read");
    public static final Type<MarkReadPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, MarkReadPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, MarkReadPayload::mailId,
            MarkReadPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

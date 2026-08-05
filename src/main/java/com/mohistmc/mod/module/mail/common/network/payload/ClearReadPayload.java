package com.mohistmc.mod.module.mail.common.network.payload;

import com.mohistmc.mod.MohistMC;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端 → 服务端：清空已读且已领取的邮件（客户端先弹确认 Modal 再发）
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public record ClearReadPayload() implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MohistMC.MODID, "clear_read_mail");
    public static final Type<ClearReadPayload> TYPE = new Type<>(ID);
    public static final ClearReadPayload INSTANCE = new ClearReadPayload();
    public static final StreamCodec<RegistryFriendlyByteBuf, ClearReadPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

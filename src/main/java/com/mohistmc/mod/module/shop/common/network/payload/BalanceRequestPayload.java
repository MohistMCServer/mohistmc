package com.mohistmc.mod.module.shop.common.network.payload;

import com.mohistmc.mod.MohistMC;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端 → 服务端：请求当前余额（ESC 界面打开时发送，回 BalanceSyncPayload）
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public record BalanceRequestPayload() implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MohistMC.MODID, "balance_request");
    public static final Type<BalanceRequestPayload> TYPE = new Type<>(ID);
    public static final BalanceRequestPayload INSTANCE = new BalanceRequestPayload();
    public static final StreamCodec<RegistryFriendlyByteBuf, BalanceRequestPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

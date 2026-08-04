package com.mohistmc.mod.module.shop.common.network.payload;

import com.mohistmc.mod.MohistMC;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端 → 客户端：同步玩家当前余额（响应 BalanceRequestPayload）
 *
 * @param balance 当前余额
 * @author Mgazul
 * @date 2026/8/5
 */
public record BalanceSyncPayload(int balance) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MohistMC.MODID, "balance_sync");
    public static final Type<BalanceSyncPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, BalanceSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, BalanceSyncPayload::balance,
            BalanceSyncPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

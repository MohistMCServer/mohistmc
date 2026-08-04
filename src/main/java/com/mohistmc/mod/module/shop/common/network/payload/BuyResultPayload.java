package com.mohistmc.mod.module.shop.common.network.payload;

import com.mohistmc.mod.MohistMC;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端 → 客户端：购买结果（成功时带新余额；失败时 message 为提示文案）
 *
 * @param success    是否成功
 * @param newBalance 最新余额（成功/失败都会返回，客户端据此刷新显示）
 * @param message    提示文案（成功/失败的本地化 key 或文字）
 * @param stock      对应商品剩余库存（-1 = 无限；0 = 售罄），客户端刷新显示
 * @author Mgazul
 * @date 2026/8/5
 */
public record BuyResultPayload(boolean success, int newBalance, String message, int stock) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MohistMC.MODID, "buy_result");
    public static final Type<BuyResultPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, BuyResultPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, BuyResultPayload::success,
            ByteBufCodecs.INT, BuyResultPayload::newBalance,
            ByteBufCodecs.STRING_UTF8, BuyResultPayload::message,
            ByteBufCodecs.INT, BuyResultPayload::stock,
            BuyResultPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

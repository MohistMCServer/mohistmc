package com.mohistmc.mod.module.shop.common.network.payload;

import com.mohistmc.mod.MohistMC;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端 → 服务端：购买请求（服务端按 id 校验商品与余额，为权威）
 *
 * @param itemId   商品 ID
 * @param quantity 购买数量（1~64，服务端会再次钳制）
 * @author Mgazul
 * @date 2026/8/5
 */
public record BuyPayload(int itemId, int quantity) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MohistMC.MODID, "buy");
    public static final Type<BuyPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, BuyPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, BuyPayload::itemId,
            ByteBufCodecs.INT, BuyPayload::quantity,
            BuyPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

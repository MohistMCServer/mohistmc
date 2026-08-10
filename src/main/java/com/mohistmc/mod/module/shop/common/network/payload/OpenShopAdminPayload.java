package com.mohistmc.mod.module.shop.common.network.payload;

import com.mohistmc.mod.MohistMC;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端 → 客户端：打开商店管理后台（管理员编辑商品）
 *
 * @author Mgazul
 * @date 2026/8/10
 */
public record OpenShopAdminPayload() implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MohistMC.MODID, "open_shop_admin");
    public static final Type<OpenShopAdminPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenShopAdminPayload> STREAM_CODEC = StreamCodec.unit(new OpenShopAdminPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
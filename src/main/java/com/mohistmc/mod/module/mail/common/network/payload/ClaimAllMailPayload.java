package com.mohistmc.mod.module.mail.common.network.payload;

import com.mohistmc.mod.MohistMC;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端 → 服务端：一键领取所有可领邮件的附件（背包放不下的按封跳过）
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public record ClaimAllMailPayload() implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MohistMC.MODID, "claim_all_mail");
    public static final Type<ClaimAllMailPayload> TYPE = new Type<>(ID);
    public static final ClaimAllMailPayload INSTANCE = new ClaimAllMailPayload();
    public static final StreamCodec<RegistryFriendlyByteBuf, ClaimAllMailPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

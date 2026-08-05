package com.mohistmc.mod.module.mail.common.network.payload;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * 发信附件选择：背包槽位 + 该槽整堆副本。
 * <p>服务端发送时按槽位核对物品与数量并扣减（防复制），槽位必须落在发送者主背包（0-35）。
 *
 * @param slot  背包槽位索引（主背包）
 * @param stack 该槽物品副本（含数量）
 * @author Mgazul
 * @date 2026/8/5
 */
public record AttachmentSelection(int slot, ItemStack stack) {

    public static final StreamCodec<RegistryFriendlyByteBuf, AttachmentSelection> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, AttachmentSelection::slot,
            ItemStack.OPTIONAL_STREAM_CODEC, AttachmentSelection::stack,
            AttachmentSelection::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, List<AttachmentSelection>> LIST_STREAM_CODEC =
            STREAM_CODEC.apply(ByteBufCodecs.collection(NonNullList::createWithCapacity));
}

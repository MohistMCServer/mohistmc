package com.mohistmc.mod.module.shop.common.attachment;

import com.mohistmc.mod.MohistMC;
import java.util.function.Supplier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 商店模块的 Attachment 注册表
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public final class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MohistMC.MODID);

    /** 玩家数字余额（默认 0，注册 serializer 后随玩家存档自动持久化） */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerBalanceData>> PLAYER_BALANCE =
            ATTACHMENT_TYPES.register("player_balance", () -> AttachmentType.serializable((Supplier<PlayerBalanceData>) PlayerBalanceData::new).build());

    private ModAttachments() {
    }
}

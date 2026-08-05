package com.mohistmc.mod.module.mail.common.attachment;

import com.mohistmc.mod.MohistMC;
import com.mohistmc.mod.module.mail.common.MailboxData;
import java.util.function.Supplier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 邮箱模块的 Attachment 注册表
 * <p>邮箱挂在主世界 ServerLevel 上（{@code server.overworld().getData(LEVEL_MAILBOX)}），
 * 随世界存档自动持久化——这是支持离线收信的关键（数据不依赖玩家是否在线/是否登录过）。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public final class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MohistMC.MODID);

    /** 服务器级邮箱数据（所有玩家的邮件，按收件人 UUID 分桶） */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<MailboxData>> LEVEL_MAILBOX =
            ATTACHMENT_TYPES.register("level_mailbox", () -> AttachmentType.serializable((Supplier<MailboxData>) MailboxData::new).build());

    private ModAttachments() {
    }
}

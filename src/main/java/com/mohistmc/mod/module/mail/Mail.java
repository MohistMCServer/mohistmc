package com.mohistmc.mod.module.mail;

import com.mohistmc.mod.module.mail.common.Mailbox;
import com.mohistmc.mod.module.mail.common.attachment.ModAttachments;
import com.mohistmc.mod.module.mail.common.command.ModCommands;
import com.mohistmc.mod.module.mail.common.network.payload.UnreadNotificationPayload;
import com.mohistmc.mod.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * 邮箱模块入口（由 MohistMC 构造器实例化）
 * <p>网络包注册（MailNetworking）依赖 @EventBusSubscriber 自动注册；
 * 此处只挂 Attachment、命令与登录未读提醒。邮箱数据挂在主世界 ServerLevel
 * Attachment 上（{@code server.overworld().getData(LEVEL_MAILBOX)}），支持离线收信。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public class Mail {

    public Mail(IEventBus modEventBus, ModContainer modContainer) {
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(ModCommands::register);
        // 登录时若有未读邮件，发未读提醒（客户端热键栏提示）
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedInEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                int unread = Mailbox.unreadCount(Mailbox.of(player.level().getServer()), player.getUUID());
                if (unread > 0) {
                    NetworkHandler.sendToClientPlayer(new UnreadNotificationPayload(unread), player);
                }
            }
        });
    }
}

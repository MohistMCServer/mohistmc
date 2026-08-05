package com.mohistmc.mod.module.mail.client.network;

import com.mohistmc.mod.module.mail.client.gui.MailComposeScreen;
import com.mohistmc.mod.module.mail.client.gui.MailScreen;
import com.mohistmc.mod.module.mail.common.network.payload.MailboxSyncPayload;
import com.mohistmc.mod.module.mail.common.network.payload.OpenBroadcastPayload;
import com.mohistmc.mod.module.mail.common.network.payload.OpenComposePayload;
import com.mohistmc.mod.module.mail.common.network.payload.OpenMailboxPayload;
import com.mohistmc.mod.module.mail.common.network.payload.UnreadNotificationPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 邮箱模块客户端收包处理（setScreen/改 UI 必须 enqueueWork 到渲染线程）
 *
 * @author Mgazul
 * @date 2026/8/5
 */
@OnlyIn(Dist.CLIENT)
public final class MailClientPayloadHandler {

    private MailClientPayloadHandler() {
    }

    /** 服务端请求打开邮箱（附完整邮件列表） */
    public static void handleOpenMailbox(OpenMailboxPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> Minecraft.getInstance().gui.setScreen(new MailScreen(payload.mails())));
    }

    /** 服务端请求打开发信附件选择界面（/mail send 触发） */
    public static void handleOpenCompose(OpenComposePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> Minecraft.getInstance().gui.setScreen(
                new MailComposeScreen(payload.recipientName(), payload.message())));
    }

    /** 服务端请求打开发信附件选择界面（群发模式，/mail sendall 触发） */
    public static void handleOpenBroadcast(OpenBroadcastPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> Minecraft.getInstance().gui.setScreen(
                new MailComposeScreen(payload.message())));
    }

    /** 邮箱操作结果：当前若在邮箱界面则重建列表并弹提示 */
    public static void handleMailboxSync(MailboxSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().gui.screen() instanceof MailScreen mailScreen) {
                mailScreen.handleMailboxSync(payload);
            }
        });
    }

    /** 登录未读提醒（热键栏提示，仿余额同步模式） */
    public static void handleUnreadNotification(UnreadNotificationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = Minecraft.getInstance().player;
            if (player != null) {
                player.sendOverlayMessage(
                        Component.translatable("command.mohistmc.mail.notify", payload.unread()));
            }
        });
    }
}

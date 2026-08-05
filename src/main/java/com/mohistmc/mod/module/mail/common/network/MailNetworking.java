package com.mohistmc.mod.module.mail.common.network;

import com.mohistmc.mod.MohistMC;
import com.mohistmc.mod.module.mail.client.network.MailClientPayloadHandler;
import com.mohistmc.mod.module.mail.common.MailEntry;
import com.mohistmc.mod.module.mail.common.Mailbox;
import com.mohistmc.mod.module.mail.common.network.payload.AttachmentSelection;
import com.mohistmc.mod.module.mail.common.network.payload.ClaimAllMailPayload;
import com.mohistmc.mod.module.mail.common.network.payload.ClaimMailPayload;
import com.mohistmc.mod.module.mail.common.network.payload.ClearReadPayload;
import com.mohistmc.mod.module.mail.common.network.payload.MailboxSyncPayload;
import com.mohistmc.mod.module.mail.common.network.payload.MarkReadPayload;
import com.mohistmc.mod.module.mail.common.network.payload.OpenBroadcastPayload;
import com.mohistmc.mod.module.mail.common.network.payload.OpenComposePayload;
import com.mohistmc.mod.module.mail.common.network.payload.OpenMailboxPayload;
import com.mohistmc.mod.module.mail.common.network.payload.OpenMailboxRequestPayload;
import com.mohistmc.mod.module.mail.common.network.payload.SendBroadcastPayload;
import com.mohistmc.mod.module.mail.common.network.payload.SendMailPayload;
import com.mohistmc.mod.module.mail.common.network.payload.UnreadNotificationPayload;
import java.util.List;
import java.util.UUID;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * 邮箱网络包注册与处理（服务端校验为权威：领取归属、背包 canFit、清空条件全部在服务端判定）
 *
 * @author Mgazul
 * @date 2026/8/5
 */
@EventBusSubscriber(modid = MohistMC.MODID)
public class MailNetworking {

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(OpenMailboxPayload.TYPE, OpenMailboxPayload.STREAM_CODEC, MailClientPayloadHandler::handleOpenMailbox);
        registrar.playToClient(OpenComposePayload.TYPE, OpenComposePayload.STREAM_CODEC, MailClientPayloadHandler::handleOpenCompose);
        registrar.playToClient(OpenBroadcastPayload.TYPE, OpenBroadcastPayload.STREAM_CODEC, MailClientPayloadHandler::handleOpenBroadcast);
        registrar.playToServer(OpenMailboxRequestPayload.TYPE, OpenMailboxRequestPayload.STREAM_CODEC, ServerPayloadHandler::handleOpenRequest);
        registrar.playToServer(SendMailPayload.TYPE, SendMailPayload.STREAM_CODEC, ServerPayloadHandler::handleSend);
        registrar.playToServer(SendBroadcastPayload.TYPE, SendBroadcastPayload.STREAM_CODEC, ServerPayloadHandler::handleBroadcast);
        registrar.playToServer(MarkReadPayload.TYPE, MarkReadPayload.STREAM_CODEC, ServerPayloadHandler::handleMarkRead);
        registrar.playToServer(ClaimMailPayload.TYPE, ClaimMailPayload.STREAM_CODEC, ServerPayloadHandler::handleClaim);
        registrar.playToServer(ClaimAllMailPayload.TYPE, ClaimAllMailPayload.STREAM_CODEC, ServerPayloadHandler::handleClaimAll);
        registrar.playToServer(ClearReadPayload.TYPE, ClearReadPayload.STREAM_CODEC, ServerPayloadHandler::handleClearRead);
        registrar.playToClient(MailboxSyncPayload.TYPE, MailboxSyncPayload.STREAM_CODEC, MailClientPayloadHandler::handleMailboxSync);
        registrar.playToClient(UnreadNotificationPayload.TYPE, UnreadNotificationPayload.STREAM_CODEC, MailClientPayloadHandler::handleUnreadNotification);
    }

    public static class ServerPayloadHandler {

        /** ESC 菜单请求打开邮箱：复用 /mail 逻辑（回全量列表，不改已读状态） */
        public static void handleOpenRequest(OpenMailboxRequestPayload payload, IPayloadContext context) {
            ServerPlayer player = (ServerPlayer) context.player();
            context.reply(new OpenMailboxPayload(
                    Mailbox.listFor(Mailbox.of(player.level().getServer()), player.getUUID())));
        }

        /** 选中查看单封：标记该封已读（仅自己桶内，防跨玩家） */
        public static void handleMarkRead(MarkReadPayload payload, IPayloadContext context) {
            Mailbox.markRead((ServerPlayer) context.player(), payload.mailId());
        }

        /**
         * 发信（附件选择界面确认）：权限校验 + 重新解析收件人 + 槽位核对（防伪造/防复制），
         * 校验通过后扣减背包再发信。失败均通过系统消息提示发送者。
         */
        public static void handleSend(SendMailPayload payload, IPayloadContext context) {
            ServerPlayer sender = (ServerPlayer) context.player();
            if (!Commands.LEVEL_GAMEMASTERS.check(sender.permissions())) {
                sender.sendSystemMessage(Component.translatable("command.mohistmc.mail.send.no_permission"));
                return;
            }
            if (payload.message().trim().isEmpty()) {
                sender.sendSystemMessage(Component.translatable("command.mohistmc.mail.send.empty"));
                return;
            }
            var server = sender.level().getServer();
            UUID recipient = Mailbox.resolveRecipient(server, payload.recipientName());
            if (recipient == null) {
                sender.sendSystemMessage(Component.translatable(
                        "command.mohistmc.mail.send.player_not_found", payload.recipientName()));
                return;
            }

            // 校验附件槽位与数量（与当前背包一致才允许扣减，防止复制/伪造）
            var inventory = sender.getInventory();
            for (AttachmentSelection sel : payload.attachments()) {
                if (sel.slot() < 0 || sel.slot() >= inventory.getContainerSize()) {
                    sender.sendSystemMessage(Component.translatable("command.mohistmc.mail.send.slot_mismatch"));
                    return;
                }
                ItemStack inSlot = inventory.getItem(sel.slot());
                if (!ItemStack.isSameItemSameComponents(inSlot, sel.stack())
                        || inSlot.getCount() < sel.stack().getCount()) {
                    sender.sendSystemMessage(Component.translatable("command.mohistmc.mail.send.slot_mismatch"));
                    return;
                }
            }
            List<ItemStack> attachments = payload.attachments().stream().map(AttachmentSelection::stack).toList();
            // 发送者名：自定义（NPC/系统名义）或默认玩家名
            String senderName = resolveSenderName(sender, payload.senderName());

            // 发信（无上限），成功后才扣减背包
            Mailbox.send(server, senderName, recipient, payload.message(), attachments);
            for (AttachmentSelection sel : payload.attachments()) {
                inventory.getItem(sel.slot()).shrink(sel.stack().getCount());
            }
            ServerPlayer online = server.getPlayerList().getPlayerByName(payload.recipientName());
            Component receiverName = online != null
                    ? online.getDisplayName()
                    : Component.literal(payload.recipientName());
            if (!attachments.isEmpty()) {
                ItemStack first = attachments.get(0);
                sender.sendSystemMessage(Component.translatable("command.mohistmc.mail.send.success_attachment",
                        receiverName, first.getHoverName(), first.getCount()));
            } else {
                sender.sendSystemMessage(Component.translatable("command.mohistmc.mail.send.success", receiverName));
            }
            if (online != null) {
                online.sendSystemMessage(Component.translatable(
                        "command.mohistmc.mail.received", senderName));
            }
        }

        /**
         * 群发（sendall 附件选择界面确认）：权限校验 + 槽位核对（防伪造/防复制），
         * 校验通过后先群发再扣减背包，收件人为所有玩家（含曾登录过的离线玩家）。
         */
        public static void handleBroadcast(SendBroadcastPayload payload, IPayloadContext context) {
            ServerPlayer sender = (ServerPlayer) context.player();
            if (!Commands.LEVEL_GAMEMASTERS.check(sender.permissions())) {
                sender.sendSystemMessage(Component.translatable("command.mohistmc.mail.send.no_permission"));
                return;
            }
            if (payload.message().trim().isEmpty()) {
                sender.sendSystemMessage(Component.translatable("command.mohistmc.mail.send.empty"));
                return;
            }
            var server = sender.level().getServer();
            List<UUID> recipients = Mailbox.allPlayerUuids(server);
            if (recipients.isEmpty()) {
                sender.sendSystemMessage(Component.translatable("command.mohistmc.mail.sendall.no_players"));
                return;
            }

            // 校验附件槽位与数量
            var inventory = sender.getInventory();
            for (AttachmentSelection sel : payload.attachments()) {
                if (sel.slot() < 0 || sel.slot() >= inventory.getContainerSize()) {
                    sender.sendSystemMessage(Component.translatable("command.mohistmc.mail.send.slot_mismatch"));
                    return;
                }
                ItemStack inSlot = inventory.getItem(sel.slot());
                if (!ItemStack.isSameItemSameComponents(inSlot, sel.stack())
                        || inSlot.getCount() < sel.stack().getCount()) {
                    sender.sendSystemMessage(Component.translatable("command.mohistmc.mail.send.slot_mismatch"));
                    return;
                }
            }
            List<ItemStack> attachments = payload.attachments().stream().map(AttachmentSelection::stack).toList();
            String senderName = resolveSenderName(sender, payload.senderName());

            int sent = 0;
            for (UUID uuid : recipients) {
                if (Mailbox.send(server, senderName, uuid, payload.message(), attachments) == null) {
                    sent++;
                }
            }
            // 至少成功一封才扣减背包（全失败不吞物品）
            if (sent > 0) {
                for (AttachmentSelection sel : payload.attachments()) {
                    inventory.getItem(sel.slot()).shrink(sel.stack().getCount());
                }
            }
            final int sentFinal = sent;
            sender.sendSystemMessage(Component.translatable(
                    "command.mohistmc.mail.sendall.success", sentFinal, recipients.size() - sentFinal));
        }

        /** 发送者名：空 → 玩家自己的名字；自定义时截断到 32 字符（NPC/系统名义） */
        private static String resolveSenderName(ServerPlayer sender, String custom) {
            if (custom == null || custom.isBlank()) {
                return sender.getGameProfile().name();
            }
            return custom.length() > 32 ? custom.substring(0, 32) : custom;
        }

        /** 领取单封：服务端校验归属与背包，回全量列表 + 结果 */
        public static void handleClaim(ClaimMailPayload payload, IPayloadContext context) {
            ServerPlayer player = (ServerPlayer) context.player();
            String failKey = Mailbox.claimSingle(player, payload.mailId());
            if (failKey == null) {
                context.reply(new MailboxSyncPayload(true, "gui.mohistmc.mail.result.claimed", 1, 0,
                        Mailbox.listFor(Mailbox.of(player.level().getServer()), player.getUUID())));
            } else {
                context.reply(new MailboxSyncPayload(false, failKey, 0, 0,
                        Mailbox.listFor(Mailbox.of(player.level().getServer()), player.getUUID())));
            }
        }

        /** 领取所有：按封尝试（背包满跳过），回成功/跳过计数 + 全量列表（无可领时回专用提示键） */
        public static void handleClaimAll(ClaimAllMailPayload payload, IPayloadContext context) {
            ServerPlayer player = (ServerPlayer) context.player();
            int[] result = Mailbox.claimAll(player);
            String message = result[0] == 0 && result[1] == 0
                    ? "gui.mohistmc.mail.result.none"
                    : "gui.mohistmc.mail.result.claimed";
            context.reply(new MailboxSyncPayload(true, message,
                    result[0], result[1], Mailbox.listFor(Mailbox.of(player.level().getServer()), player.getUUID())));
        }

        /** 清空已读且已领取的邮件：回全量列表 + 结果 */
        public static void handleClearRead(ClearReadPayload payload, IPayloadContext context) {
            ServerPlayer player = (ServerPlayer) context.player();
            context.reply(new MailboxSyncPayload(true, "gui.mohistmc.mail.result.cleared", 0, 0,
                    Mailbox.clearRead(player)));
        }
    }
}

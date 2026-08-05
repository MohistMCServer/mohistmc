package com.mohistmc.mod.module.mail.common.command;

import com.mohistmc.mod.module.mail.common.Mailbox;
import com.mohistmc.mod.module.mail.common.network.payload.OpenBroadcastPayload;
import com.mohistmc.mod.module.mail.common.network.payload.OpenComposePayload;
import com.mohistmc.mod.module.mail.common.network.payload.OpenMailboxPayload;
import com.mohistmc.mod.network.NetworkHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.List;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * 邮箱命令：
 * <ul>
 *   <li>{@code /mail} —— 所有玩家：打开邮箱界面（回传列表，已读状态保持，选中查看才逐封标记）</li>
 *   <li>{@code /mail send <玩家> <消息...>} —— 管理员（同 /give 权限）：发信；
 *       玩家一律打开附件选择界面（背包多选，不选即纯文本，不识别手持物品防误发）；
 *       控制台为纯文本。</li>
 *   <li>{@code /mail sendall <消息...>} —— 管理员：群发给所有玩家（在线 + 曾登录过的离线玩家）；
 *       玩家触发时打开群发界面（可自定义发件人、背包多选附件，不选即纯文本）；控制台为纯文本。</li>
 * </ul>
 * 收件人支持离线：在线优先 {@code getPlayerByName}，否则走 {@code nameToIdCache}
 * （MC 26.2 已无 GameProfileCache；未命中会回退为离线推导 UUID——离线服可用，
 * 在线服发给从未登录过的名字会产生幻影邮箱，属已知限制）。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public final class ModCommands {

    /** 控制台发信时的发送者名 */
    private static final String CONSOLE_NAME = "控制台";

    private ModCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        final CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("mail")
                .executes(ctx -> openMailbox(ctx.getSource()))
                .then(Commands.literal("send")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)) // 管理员权限（同 /give）
                        .then(Commands.argument("player", StringArgumentType.word())
                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(ctx -> sendMail(ctx.getSource(),
                                                StringArgumentType.getString(ctx, "player"),
                                                StringArgumentType.getString(ctx, "message"))))))
                .then(Commands.literal("sendall")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(ctx -> sendAllMail(ctx.getSource(),
                                        StringArgumentType.getString(ctx, "message"))))));
    }

    /** /mail：回传邮件列表（不改已读状态，选中查看时才逐封标记），客户端打开界面 */
    private static int openMailbox(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.translatable("command.mohistmc.mail.open.player_only"));
            return 0;
        }
        NetworkHandler.sendToClientPlayer(new OpenMailboxPayload(
                Mailbox.listFor(Mailbox.of(player.level().getServer()), player.getUUID())), player);
        return 1;
    }

    /**
     * /mail send：玩家一律打开附件选择界面（背包多选，不选即纯文本——不再识别手持物品，
     * 防止误把手持物品发出）；控制台 → 纯文本。
     */
    private static int sendMail(CommandSourceStack source, String playerName, String message) {
        if (message.trim().isEmpty()) {
            source.sendFailure(Component.translatable("command.mohistmc.mail.send.empty"));
            return 0;
        }
        var server = source.getServer();
        ServerPlayer sender = source.getPlayer();
        // 玩家发信：一律走附件选择界面，由客户端从背包多选物品后经 SendMailPayload 发送
        if (sender != null) {
            NetworkHandler.sendToClientPlayer(new OpenComposePayload(playerName, message), sender);
            return 1;
        }

        // 控制台：纯文本
        UUID recipient = Mailbox.resolveRecipient(server, playerName);
        if (recipient == null) {
            source.sendFailure(Component.translatable("command.mohistmc.mail.send.player_not_found", playerName));
            return 0;
        }
        ServerPlayer online = server.getPlayerList().getPlayerByName(playerName);

        Mailbox.send(server, CONSOLE_NAME, recipient, message, List.of());

        Component receiverName = online != null
                ? online.getDisplayName()
                : Component.literal(playerName);
        source.sendSuccess(() -> Component.translatable("command.mohistmc.mail.send.success", receiverName), true);
        // 收件人在线：即时提醒
        if (online != null) {
            online.sendSystemMessage(Component.translatable("command.mohistmc.mail.received", CONSOLE_NAME));
        }
        return 1;
    }

    /**
     * /mail sendall <消息...>：玩家 → 打开群发附件选择界面（背包多选、可自定义发件人，
     * 经 SendBroadcastPayload 发送）；控制台 → 纯文本群发。
     */
    private static int sendAllMail(CommandSourceStack source, String message) {
        if (message.trim().isEmpty()) {
            source.sendFailure(Component.translatable("command.mohistmc.mail.send.empty"));
            return 0;
        }
        var server = source.getServer();
        ServerPlayer sender = source.getPlayer();
        // 玩家群发：一律走附件选择界面（不选即纯文本，可自定义发件人）
        if (sender != null) {
            NetworkHandler.sendToClientPlayer(new OpenBroadcastPayload(message), sender);
            return 1;
        }
        // 控制台：纯文本群发
        List<UUID> recipients = Mailbox.allPlayerUuids(server);
        if (recipients.isEmpty()) {
            source.sendFailure(Component.translatable("command.mohistmc.mail.sendall.no_players"));
            return 0;
        }
        int sent = 0;
        for (UUID uuid : recipients) {
            if (Mailbox.send(server, CONSOLE_NAME, uuid, message, List.of()) == null) {
                sent++;
            }
        }
        final int sentFinal = sent;
        source.sendSuccess(() -> Component.translatable("command.mohistmc.mail.sendall.success", sentFinal, 0), true);
        return 1;
    }
}

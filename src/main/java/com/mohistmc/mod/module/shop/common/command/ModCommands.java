package com.mohistmc.mod.module.shop.common.command;

import com.mohistmc.mod.module.shop.common.attachment.PlayerBalance;
import com.mohistmc.mod.module.shop.common.data.Currency;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * 商店余额命令：/money give &lt;玩家&gt; &lt;数量&gt;（余额查询见 ESC 界面与商店界面）
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public final class ModCommands {

    private ModCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        final CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("money")
                .then(Commands.literal("give")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)) // 管理员权限（同 /give）
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, Integer.MAX_VALUE - 1))
                                        .executes(ctx -> give(ctx.getSource(),
                                                EntityArgument.getPlayer(ctx, "player"),
                                                IntegerArgumentType.getInteger(ctx, "amount")))))));
    }

    private static int give(CommandSourceStack source, ServerPlayer target, int amount) {
        // long 计算防溢出（amount 上限已是 Integer.MAX_VALUE - 1）
        PlayerBalance.add(target, amount);
        int newBalance = PlayerBalance.get(target);
        source.sendSuccess(() -> Component.translatable("command.mohistmc.money.give.success",
                target.getDisplayName(), amount, Currency.displayName(), newBalance), true);
        target.sendSystemMessage(Component.translatable("command.mohistmc.money.give.receiver",
                amount, Currency.displayName(), newBalance));
        return 1;
    }
}

package com.mohistmc.mod.module.shop.common.attachment;

import net.minecraft.world.entity.player.Player;

/**
 * 玩家余额读写工具
 * <p>getData 无值时自动存默认值；就地修改后由 Attachment 序列化随玩家存档落盘。
 * 注意：金额计算请使用 long 防止溢出。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public final class PlayerBalance {

    private PlayerBalance() {
    }

    public static int get(Player player) {
        return player.getData(ModAttachments.PLAYER_BALANCE).get();
    }

    public static void set(Player player, int balance) {
        player.getData(ModAttachments.PLAYER_BALANCE).set(Math.max(0, balance));
    }

    public static void add(Player player, int amount) {
        // long 计算防 int 溢出（余额接近上限时累计会回绕为负）
        long next = (long) get(player) + amount;
        set(player, next > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) next);
    }

    public static void subtract(Player player, int amount) {
        // 调用方已保证余额充足；long 计算防溢出
        long next = (long) get(player) - amount;
        set(player, next < 0 ? 0 : (int) next);
    }
}

package com.mohistmc.mod.module.shop.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/**
 * 商店购买会话（服务端内存态）— 防伪造购买数据包：
 * 玩家必须右键售货机打开商店（登记会话 + 记录售货机位置），
 * 购买时校验会话存在且玩家仍在售货机附近，否则拒绝。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public final class ShopSession {

    /** 玩家 → 打开的售货机位置 */
    private static final Map<UUID, BlockPos> OPEN_SHOPS = new HashMap<>();

    /** 允许购买的最大距离（格） */
    private static final int MAX_DIST = 8;

    private ShopSession() {
    }

    /** 打开商店（右键售货机时由服务端登记） */
    public static void open(Player player, BlockPos vendingPos) {
        OPEN_SHOPS.put(player.getUUID(), vendingPos.immutable());
    }

    /** 关闭/清理会话（登出等） */
    public static void close(Player player) {
        OPEN_SHOPS.remove(player.getUUID());
    }

    /** 是否允许购买：已打开商店且仍在售货机附近 */
    public static boolean canBuy(Player player) {
        BlockPos pos = OPEN_SHOPS.get(player.getUUID());
        if (pos == null) return false;
        return player.blockPosition().closerThan(pos, MAX_DIST);
    }
}

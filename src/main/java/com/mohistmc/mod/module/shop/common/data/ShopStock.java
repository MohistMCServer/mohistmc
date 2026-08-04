package com.mohistmc.mod.module.shop.common.data;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 商品库存（服务端内存态）— 仅记录有限库存商品；-1 表示无限
 * <p>支持可选自动补货（现实时间）：配置 {@link RestockCycle#DAILY} 每天 04:00 重置、
 * {@link RestockCycle#WEEKLY} 每周一 04:00 重置；购买时惰性检查（跨过重置点即补满，无需 tick）。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public final class ShopStock {

    /** 补货重置时刻（每日/每周一 04:00） */
    private static final LocalTime RESET_TIME = LocalTime.of(4, 0);

    /** 商品 id → 库存状态（仅有限库存商品） */
    private static final Map<Integer, StockState> STATES = new HashMap<>();
    private static boolean initialized;

    private ShopStock() {
    }

    private static final class StockState {
        final int initialStock;
        final RestockCycle restockCycle;
        int remaining;
        LocalDateTime lastReset;

        StockState(int initialStock, RestockCycle restockCycle) {
            this.initialStock = initialStock;
            this.restockCycle = restockCycle;
            this.remaining = initialStock;
        }
    }

    private static void ensureInit() {
        if (initialized) return;
        for (var product : ShopData.PRODUCTS) {
            if (product.stock() > 0) {
                STATES.put(product.id(), new StockState(product.stock(), product.restockCycle()));
            }
        }
        initialized = true;
    }

    /** 到期补货：已跨过下一个重置点（每日 04:00 / 每周一 04:00）则补满 */
    private static void maybeRestock(StockState state) {
        if (state.restockCycle == RestockCycle.NONE) return;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime resetPoint = switch (state.restockCycle) {
            case DAILY -> LocalDateTime.of(now.toLocalDate(), RESET_TIME);                      // 今天 04:00
            case WEEKLY -> LocalDateTime.of(now.toLocalDate().with(DayOfWeek.MONDAY), RESET_TIME); // 本周一 04:00
            case NONE -> null;
        };
        if (state.lastReset == null || state.lastReset.isBefore(resetPoint)) {
            state.remaining = state.initialStock;
            state.lastReset = now;
        }
    }

    /** 剩余库存（-1 = 无限） */
    public static int remaining(int productId) {
        ensureInit();
        StockState state = STATES.get(productId);
        if (state == null) return -1;
        maybeRestock(state);
        return state.remaining;
    }

    /** 是否还有足够库存（无限恒 true）；到期自动补货后判断 */
    public static boolean has(int productId, int qty) {
        int left = remaining(productId);
        return left < 0 || left >= qty;
    }

    /** 扣减库存（无限商品不处理；到期自动补货后扣减） */
    public static void consume(int productId, int qty) {
        ensureInit();
        StockState state = STATES.get(productId);
        if (state == null) return;
        maybeRestock(state);
        state.remaining = Math.max(0, state.remaining - qty);
    }
}

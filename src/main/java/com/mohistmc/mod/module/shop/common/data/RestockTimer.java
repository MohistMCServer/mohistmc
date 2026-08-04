package com.mohistmc.mod.module.shop.common.data;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 补货倒计时工具（与 {@link ShopStock} 的 04:00 重置时刻保持一致）
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public final class RestockTimer {

    /** 补货重置时刻（每日/每周一 04:00） */
    private static final LocalTime RESET_TIME = LocalTime.of(4, 0);

    private RestockTimer() {
    }

    /** 距下次补货的剩余时间（NONE 返回 ZERO） */
    public static Duration remaining(RestockCycle cycle) {
        if (cycle == RestockCycle.NONE) return Duration.ZERO;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reset = switch (cycle) {
            case DAILY -> {
                var t = LocalDateTime.of(now.toLocalDate(), RESET_TIME); // 今天 04:00
                yield t.isAfter(now) ? t : t.plusDays(1);                // 已过则明天
            }
            case WEEKLY -> {
                var t = LocalDateTime.of(now.toLocalDate().with(DayOfWeek.MONDAY), RESET_TIME); // 本周一 04:00
                yield t.isAfter(now) ? t : t.plusWeeks(1);               // 已过则下周一
            }
            case NONE -> null;
        };
        return reset == null ? Duration.ZERO : Duration.between(now, reset);
    }

    /** 紧凑格式（格子徽章用）：2天 / 23时 / 45分 */
    public static String formatCompact(Duration d) {
        long days = d.toDays();
        long hours = d.toHours() % 24;
        long mins = d.toMinutes() % 60;
        if (days > 0) return days + "天";
        if (hours > 0) return hours + "时";
        return Math.max(1, mins) + "分";
    }

    /** 精准格式（动态倒计时）：6天5小时23分45秒 / 5小时23分45秒 / 23分45秒 / 45秒 */
    public static String formatPrecise(Duration d) {
        long totalSec = Math.max(0, d.getSeconds());
        long days = totalSec / 86400;
        long hours = (totalSec % 86400) / 3600;
        long mins = (totalSec % 3600) / 60;
        long secs = totalSec % 60;
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("天");
        if (hours > 0 || days > 0) sb.append(hours).append("小时");
        if (mins > 0 || hours > 0 || days > 0) sb.append(mins).append("分");
        sb.append(secs).append("秒");
        return sb.toString();
    }

    /** 完整格式（详情面板用）：2天3小时 / 3小时20分钟 / 45分钟 */
    public static String formatFull(Duration d) {
        long days = d.toDays();
        long hours = d.toHours() % 24;
        long mins = d.toMinutes() % 60;
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("天");
        if (hours > 0) sb.append(hours).append("小时");
        if (mins > 0 || sb.isEmpty()) sb.append(mins).append("分钟");
        return sb.toString();
    }
}

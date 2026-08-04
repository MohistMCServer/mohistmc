package com.mohistmc.mod.module.shop.common.data;

/**
 * 自动补货周期（基于现实时间）
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public enum RestockCycle {
    /** 不补货（卖完即售罄） */
    NONE,
    /** 每天 00:00 重置 */
    DAILY,
    /** 每周一 00:00 重置 */
    WEEKLY
}

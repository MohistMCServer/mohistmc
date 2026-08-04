package com.mohistmc.mod.module.shop.common.data;

import net.minecraft.world.item.ItemStack;

/**
 * 商店商品条目
 *
 * @param id          商品唯一 ID（购买请求以此为准，服务端权威校验）
 * @param stack       商品物品（图标 + 名称来源，购买时按数量复制）
 * @param price       单价（数字余额 ¥）
 * @param category    所属类别（分类筛选用）
 * @param stock       库存数量（-1 = 无限，默认无限）
 * @param restockCycle 自动补货周期（现实时间；NONE = 不补货，默认）
 * @author Mgazul
 * @date 2026/8/5
 */
public record ShopProduct(int id, ItemStack stack, int price, ShopCategory category, int stock, RestockCycle restockCycle) {

    /** 默认无限库存、不补货 */
    public ShopProduct(int id, ItemStack stack, int price, ShopCategory category) {
        this(id, stack, price, category, -1, RestockCycle.NONE);
    }

    /** 有限库存、不补货 */
    public ShopProduct(int id, ItemStack stack, int price, ShopCategory category, int stock) {
        this(id, stack, price, category, stock, RestockCycle.NONE);
    }
}

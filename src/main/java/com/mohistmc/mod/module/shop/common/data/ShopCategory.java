package com.mohistmc.mod.module.shop.common.data;

/**
 * 商品类别（ALL 仅用于筛选 Tab）
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public enum ShopCategory {
    ALL("gui.mohistmc.shop.cat.all"),
    VEGETABLE("gui.mohistmc.shop.cat.vegetable"),
    FRUIT("gui.mohistmc.shop.cat.fruit"),
    HERB("gui.mohistmc.shop.cat.herb"),
    EQUIPMENT("gui.mohistmc.shop.cat.equipment"),
    FOOD("gui.mohistmc.shop.cat.food");

    private final String langKey;

    ShopCategory(String langKey) {
        this.langKey = langKey;
    }

    public String getLangKey() {
        return langKey;
    }
}

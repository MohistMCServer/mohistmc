package com.mohistmc.mod.module.shop.common.data;

/**
 * 商店货币类型 — 每种货币绑定一张贴图，界面以「贴图图标 + 数字」展示（替代 ¥ 符号）
 * <p>新增货币需在 {@code assets/mohistmc/textures/ui/} 提供对应贴图后加入枚举。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public enum CurrencyType {
    GOLD("currency.mohistmc.gold", "textures/ui/jinbi.png", 32);

    private final String langKey;
    private final String texturePath;
    private final int iconSize;

    CurrencyType(String langKey, String texturePath, int iconSize) {
        this.langKey = langKey;
        this.texturePath = texturePath;
        this.iconSize = iconSize;
    }

    /** 货币显示名（本地化 key） */
    public String getLangKey() {
        return langKey;
    }

    /** 货币图标贴图路径（相对 assets/mohistmc/） */
    public String getTexturePath() {
        return texturePath;
    }

    /** 图标贴图边长（方形贴图，用于缩放渲染完整图标） */
    public int getIconSize() {
        return iconSize;
    }
}

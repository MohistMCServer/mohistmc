package com.mohistmc.mod.module.shop.common.data;

import com.mohistmc.mod.MohistMC;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * 当前货币工具：图标贴图 / 显示名
 * <p>切换货币类型直接改 {@link #CURRENT}（如未来新增 {@link CurrencyType}），
 * 界面图标与货币名随之一致变化。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public final class Currency {

    /** 当前货币类型（改这里切换） */
    public static CurrencyType CURRENT = CurrencyType.GOLD;

    private Currency() {
    }

    public static CurrencyType get() {
        return CURRENT;
    }

    /** 货币图标贴图（显示价格/余额用） */
    public static Identifier iconTexture() {
        return Identifier.fromNamespaceAndPath(MohistMC.MODID, get().getTexturePath());
    }

    /** 货币图标贴图边长（缩放渲染完整图标用） */
    public static int iconSize() {
        return get().getIconSize();
    }

    /** 货币显示名（如"金币"） */
    public static Component displayName() {
        return Component.translatable(get().getLangKey());
    }
}

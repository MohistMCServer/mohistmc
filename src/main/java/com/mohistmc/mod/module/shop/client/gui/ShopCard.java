package com.mohistmc.mod.module.shop.client.gui;

import com.mohistmc.mod.api.gui.Badge;
import com.mohistmc.mod.api.gui.ScrollListItem;
import com.mohistmc.mod.module.shop.common.data.Currency;
import com.mohistmc.mod.module.shop.common.data.ShopProduct;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 商店商品格子（用于 GridScrollList 网格的正方形单元格，边长 = 列宽）：
 * 16px 图标 + 金色价格（贴底），名称由 ShopScreen 以 hover tooltip 显示，
 * 点击选中（绿色边框），数量与购买在 ShopScreen 右侧详情面板操作
 * <p>布局（w 为格子边长，GridScrollList 正方形模式下高 = w）：
 * <pre>
 * y+2      : 16×16 图标（水平居中）
 * y+w-10   : 单价 ¥xx（金色，贴底居中）
 * </pre>
 *
 * @author Mgazul
 * @date 2026/8/5
 */
@OnlyIn(Dist.CLIENT)
public class ShopCard extends ScrollListItem {

    /** 占位高度（GridScrollList 正方形模式下被忽略，实际高 = 列宽） */
    private static final int CARD_HEIGHT = 48;

    private final ShopProduct product;
    /** 点击格子时回调（参数为自身，供 Screen 记录选中态） */
    private final Consumer<ShopCard> selectAction;
    private boolean selected;
    /** 剩余库存（-1 = 无限；0 = 售罄；购买成功后由服务端回包刷新） */
    private int remaining = -1;

    public ShopCard(ShopProduct product, Consumer<ShopCard> selectAction) {
        this.product = product;
        this.selectAction = selectAction;
        setHeight(CARD_HEIGHT);
    }

    public ShopProduct getProduct() {
        return product;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    /** 刷新剩余库存（服务端购买回包） */
    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    /** 当前剩余库存（-1 = 无限） */
    public int getRemaining() {
        return remaining;
    }

    public boolean isSoldOut() {
        return remaining == 0;
    }

    @Override
    public boolean handleClick(int rx, int ry, int w) {
        selectAction.accept(this);
        return true; // 消费整格点击（滚动条由 GridScrollList 先行处理）
    }

    @Override
    public void render(GuiGraphicsExtractor g, int x, int y, int w, boolean hovered, int alpha) {
        int a = alpha & 0xFF000000; // 取 alpha 通道
        int s = w; // 正方形边长（GridScrollList 正方形模式下高 = 宽）
        boolean soldOut = isSoldOut();

        // 格子背景 + 边框（售罄变暗；选中绿色，悬停亮色）
        g.fill(x, y, x + s, y + s, a | (soldOut ? 0xFF1E1E28 : 0xFF2A2A3A));
        int border = soldOut ? 0xFF333344 : (selected ? 0xFF4CAF50 : (hovered ? 0xFF66BB6A : 0xFF444466));
        g.fill(x, y, x + s, y + 1, a | border);
        g.fill(x, y + s - 1, x + s, y + s, a | border);
        g.fill(x, y, x + 1, y + s, a | border);
        g.fill(x + s - 1, y, x + s, y + s, a | border);

        var stack = product.stack();

        // 图标 16×16（卡片区域内水平垂直居中）
        int iconX = x + (s - 16) / 2;
        int iconY = y + (s - 16) / 2;
        g.item(stack, iconX, iconY);

        // 价格徽章（卡片下方独立一行，与卡片同宽 + 边框）：售罄显示红字，否则货币图标 4px + 数字
        int badgeH = 9;
        Component badgeText;
        int badgeTextColor;
        if (soldOut) {
            badgeText = Component.translatable("gui.mohistmc.shop.soldout");
            if (font().width(badgeText) > s - 8) {
                String trimmed = font().plainSubstrByWidth(badgeText.getString(), Math.max(2, s - 10));
                badgeText = Component.literal(trimmed);
            }
            badgeTextColor = 0xFFFF5555;
        } else {
            var priceText = Component.literal(String.valueOf(product.price()));
            if (font().width(priceText) > s - 8) {
                String trimmed = font().plainSubstrByWidth(priceText.getString(), Math.max(2, s - 10));
                priceText = Component.literal(trimmed);
            }
            badgeText = priceText;
            badgeTextColor = 0xFFFFFFFF;
        }
        Badge.render(g, x, y + s + 2, s, badgeH,
                soldOut ? null : Currency.iconTexture(), 4, Currency.iconSize(),
                badgeText, soldOut ? 0xCC1A1A22 : 0xCC222222, badgeTextColor, alpha, 0, 0xFF444466, 1);
    }
}

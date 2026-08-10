package com.mohistmc.mod.module.shop.client.network;

import com.mohistmc.mod.client.gui.EscGui;
import com.mohistmc.mod.module.shop.client.gui.ShopAdminScreen;
import com.mohistmc.mod.module.shop.client.gui.ShopScreen;
import com.mohistmc.mod.module.shop.common.network.payload.BalanceSyncPayload;
import com.mohistmc.mod.module.shop.common.network.payload.BuyResultPayload;
import com.mohistmc.mod.module.shop.common.network.payload.OpenShopAdminPayload;
import com.mohistmc.mod.module.shop.common.network.payload.OpenShopPayload;
import com.mohistmc.mod.module.shop.common.network.payload.ShopDataSyncPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 商店模块客户端收包处理（setScreen/改 UI 必须 enqueueWork 到渲染线程）
 *
 * @author Mgazul
 * @date 2026/8/5
 */
@OnlyIn(Dist.CLIENT)
public final class ShopClientPayloadHandler {

    private ShopClientPayloadHandler() {
    }

    /** 服务端请求打开商店（附当前余额） */
    public static void handleOpenShop(OpenShopPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> Minecraft.getInstance().gui.setScreen(new ShopScreen(payload.balance())));
    }

    /** 服务端请求打开商店管理后台（管理员编辑商品） */
    public static void handleOpenShopAdmin(OpenShopAdminPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> Minecraft.getInstance().gui.setScreen(new ShopAdminScreen()));
    }

    /** 余额同步：ESC 界面若在显示则刷新金币行 */
    public static void handleBalanceSync(BalanceSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().gui.screen() instanceof EscGui escGui) {
                escGui.updateBalance(payload.balance());
            }
        });
    }

    /** 购买结果：当前若在商店界面则刷新余额/弹提示 */
    public static void handleBuyResult(BuyResultPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().gui.screen() instanceof ShopScreen shopScreen) {
                shopScreen.handleBuyResult(payload);
            }
        });
    }

    /** 商品目录同步：服务端编辑后广播，更新本地数据并刷新界面 */
    public static void handleDataSync(ShopDataSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            payload.applyToClient();
            // 如果当前在商店界面或管理界面，刷新显示
            var screen = Minecraft.getInstance().gui.screen();
            if (screen instanceof ShopScreen shopScreen) {
                shopScreen.rebuildWidgets();
            } else if (screen instanceof ShopAdminScreen adminScreen) {
                adminScreen.rebuildWidgets();
            }
        });
    }
}

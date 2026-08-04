package com.mohistmc.mod.module.shop.common.network;

import com.mohistmc.mod.MohistMC;
import com.mohistmc.mod.module.shop.client.network.ShopClientPayloadHandler;
import com.mohistmc.mod.module.shop.common.ShopSession;
import com.mohistmc.mod.module.shop.common.attachment.PlayerBalance;
import com.mohistmc.mod.module.shop.common.data.ShopData;
import com.mohistmc.mod.module.shop.common.data.ShopProduct;
import com.mohistmc.mod.module.shop.common.data.ShopStock;
import com.mohistmc.mod.module.shop.common.network.payload.BalanceRequestPayload;
import com.mohistmc.mod.module.shop.common.network.payload.BalanceSyncPayload;
import com.mohistmc.mod.module.shop.common.network.payload.BuyPayload;
import com.mohistmc.mod.module.shop.common.network.payload.BuyResultPayload;
import com.mohistmc.mod.module.shop.common.network.payload.OpenShopPayload;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * 商店网络包注册与处理（服务端购买校验为权威）
 *
 * @author Mgazul
 * @date 2026/8/5
 */
@EventBusSubscriber(modid = MohistMC.MODID)
public class ShopNetworking {

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(OpenShopPayload.TYPE, OpenShopPayload.STREAM_CODEC, ShopClientPayloadHandler::handleOpenShop);
        registrar.playToServer(BalanceRequestPayload.TYPE, BalanceRequestPayload.STREAM_CODEC, ServerPayloadHandler::handleBalanceRequest);
        registrar.playToClient(BalanceSyncPayload.TYPE, BalanceSyncPayload.STREAM_CODEC, ShopClientPayloadHandler::handleBalanceSync);
        registrar.playToServer(BuyPayload.TYPE, BuyPayload.STREAM_CODEC, ServerPayloadHandler::handleBuy);
        registrar.playToClient(BuyResultPayload.TYPE, BuyResultPayload.STREAM_CODEC, ShopClientPayloadHandler::handleBuyResult);
    }

    public static class ServerPayloadHandler {

        /** 余额查询：直接回当前余额 */
        public static void handleBalanceRequest(BalanceRequestPayload payload, IPayloadContext context) {
            context.reply(new BalanceSyncPayload(PlayerBalance.get(context.player())));
        }

        public static void handleBuy(BuyPayload payload, IPayloadContext context) {
            Player player = context.player();
            // 安全校验：必须右键售货机打开过商店且仍在售货机附近（防伪造购买数据包）
            if (!ShopSession.canBuy(player)) {
                context.reply(new BuyResultPayload(false, PlayerBalance.get(player), "gui.mohistmc.shop.fail.session", -1));
                return;
            }
            ShopProduct product = ShopData.getById(payload.itemId());
            if (product == null) {
                context.reply(new BuyResultPayload(false, PlayerBalance.get(player), "gui.mohistmc.shop.fail.invalid", -1));
                return;
            }
            // 服务端再次钳制数量，防作弊（上限 = 主背包格数 × 物品最大堆叠）
            int maxQty = 36 * Math.max(1, product.stack().getMaxStackSize());
            int qty = Math.max(1, Math.min(maxQty, payload.quantity()));
            long total = (long) product.price() * qty; // long 防溢出
            int balance = PlayerBalance.get(player);
            if (total > Integer.MAX_VALUE || total > balance) {
                context.reply(new BuyResultPayload(false, balance, "gui.mohistmc.shop.fail.balance", ShopStock.remaining(product.id())));
                return;
            }
            // 库存校验：有限库存不足直接拦截（到期自动补货后判断）
            if (!ShopStock.has(product.id(), qty)) {
                context.reply(new BuyResultPayload(false, balance, "gui.mohistmc.shop.fail.stock", ShopStock.remaining(product.id())));
                return;
            }
            ItemStack stack = product.stack().copy();
            stack.setCount(qty);
            // 背包放不下直接拦截：不扣款、不发物品
            if (!canFit(player.getInventory(), stack)) {
                context.reply(new BuyResultPayload(false, balance, "gui.mohistmc.shop.fail.inventory", ShopStock.remaining(product.id())));
                return;
            }
            // canFit 已保证可完整放入（主背包 36 槽）；失败分支仅作防御
            if (!player.getInventory().add(stack) || !stack.isEmpty()) {
                context.reply(new BuyResultPayload(false, balance, "gui.mohistmc.shop.fail.inventory", ShopStock.remaining(product.id())));
                return;
            }
            ShopStock.consume(product.id(), qty);
            PlayerBalance.subtract(player, (int) total);
            context.reply(new BuyResultPayload(true, PlayerBalance.get(player), "gui.mohistmc.shop.success", ShopStock.remaining(product.id())));
        }

        /**
         * 检查主背包能否完整容纳该物品（空槽 + 可堆叠槽容量）。
         * 注意：只用主背包槽（getNonEquipmentItems，36 格）——getContainerSize 含盔甲/副手（41 格），
         * 空装备槽会让 add 实际失败导致物品丢失。
         */
        private static boolean canFit(Inventory inventory, ItemStack stack) {
            ItemStack remaining = stack.copy();
            for (ItemStack slot : inventory.getNonEquipmentItems()) {
                if (slot.isEmpty()) {
                    return true; // 有空槽即可放下剩余部分
                }
                if (ItemStack.isSameItemSameComponents(slot, remaining) && slot.getCount() < slot.getMaxStackSize()) {
                    int room = slot.getMaxStackSize() - slot.getCount();
                    remaining.shrink(room);
                    if (remaining.isEmpty()) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}

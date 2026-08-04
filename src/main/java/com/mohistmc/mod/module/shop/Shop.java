package com.mohistmc.mod.module.shop;

import com.mohistmc.mod.module.shop.common.ShopSession;
import com.mohistmc.mod.module.shop.common.attachment.ModAttachments;
import com.mohistmc.mod.module.shop.common.command.ModCommands;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * 系统商店模块入口（由 MohistMC 构造器实例化）
 * <p>网络包注册（ShopNetworking）依赖 @EventBusSubscriber 自动注册；
 * VendingMachineBlock 已注册于主注册器，此处只挂命令、Attachment 与会话清理。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public class Shop {

    public Shop(IEventBus modEventBus, ModContainer modContainer) {
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(ModCommands::register);
        // 玩家登出清理购买会话
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedOutEvent.class,
                event -> ShopSession.close(event.getEntity()));
    }
}

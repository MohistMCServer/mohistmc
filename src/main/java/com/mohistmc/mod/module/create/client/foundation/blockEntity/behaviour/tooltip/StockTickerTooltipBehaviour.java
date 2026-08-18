package com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.tooltip;

import com.mohistmc.mod.module.create.client.api.goggles.IHaveHoveringInformation;
import com.mohistmc.mod.module.create.client.foundation.utility.CreateLang;
import com.mohistmc.mod.module.create.content.logistics.BigItemStack;
import com.mohistmc.mod.module.create.content.logistics.packager.InventorySummary;
import com.mohistmc.mod.module.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.mohistmc.mod.module.create.content.logistics.stockTicker.StockTickerBlockEntity.StockTickerInventory;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class StockTickerTooltipBehaviour extends TooltipBehaviour<StockTickerBlockEntity> implements IHaveHoveringInformation {
    public StockTickerTooltipBehaviour(StockTickerBlockEntity be) {
        super(be);
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        StockTickerInventory receivedPayments = blockEntity.receivedPayments;
        if (receivedPayments.isEmpty()) {
            return false;
        }
        if (!blockEntity.behaviour.mayAdministrate(Minecraft.getInstance().player)) {
            return false;
        }

        CreateLang.translate("stock_ticker.contains_payments").style(ChatFormatting.WHITE).forGoggles(tooltip);

        InventorySummary summary = new InventorySummary();
        for (int i = 0, size = receivedPayments.getContainerSize(); i < size; i++) {
            summary.add(receivedPayments.getItem(i));
        }
        for (BigItemStack entry : summary.getStacksByCount()) {
            CreateLang.builder().text(entry.stack.getHoverName().getString() + " x" + entry.count)
                .style(ChatFormatting.GREEN).forGoggles(tooltip);
        }

        CreateLang.translate("stock_ticker.click_to_retrieve").style(ChatFormatting.GRAY).forGoggles(tooltip);
        return true;
    }
}

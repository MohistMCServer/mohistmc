package com.mohistmc.mod.item;

import com.mohistmc.mod.utils.TooltipHelper;
import java.util.LinkedList;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * @author Mgazul
 * @date 2026/1/5 23:20
 */
public class XiangCaiItem extends Item {

    public XiangCaiItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getXiangCaiTooltip());
    }

    private static LinkedList<Component> getXiangCaiTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【香菜】§r"));
        tooltip.add(Component.literal("§7食用后恢复§62点§7饥饿值，具有特殊香味§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：地中海地区§r"));
        tooltip.add(Component.literal("§e 传入：汉代张骞通西域时引入§r"));
        tooltip.add(Component.literal("§e 特点：香味浓郁，常作调味用§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"香菜飘香\" —— 香菜因其独特的香味而成为重要的调味蔬菜§r"));
        return tooltip;
    }
}

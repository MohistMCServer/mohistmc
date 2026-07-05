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
 * @date 2026/1/5 23:21
 */
public class ShengJiangItem extends Item {

    public ShengJiangItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getShengJiangTooltip());
    }

    private static LinkedList<Component> getShengJiangTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【生姜】§r"));
        tooltip.add(Component.literal("§7食用后恢复§62点§7饥饿值，具有温热特性§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：东南亚地区§r"));
        tooltip.add(Component.literal("§e 栽培：中国自古栽培，药食同源§r"));
        tooltip.add(Component.literal("§e 特点：根茎辛辣，可调味或药用§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"姜是老的辣\" —— 生姜因其辛辣味和药用价值而备受推崇§r"));
        return tooltip;
    }
}

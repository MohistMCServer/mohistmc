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
public class ZiSuYeItem extends Item {

    public ZiSuYeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getZiSuYeTooltip());
    }

    private static LinkedList<Component> getZiSuYeTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【紫苏叶】§r"));
        tooltip.add(Component.literal("§7食用后恢复§62点§7饥饿值，具有特殊香味§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：中国及东南亚§r"));
        tooltip.add(Component.literal("§e 栽培：中国自古栽培，药食同源§r"));
        tooltip.add(Component.literal("§e 特点：叶片紫色，可作调料或药用§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"紫苏飘香\" —— 紫苏叶因其独特的香味和药用价值而备受推崇§r"));
        return tooltip;
    }
}

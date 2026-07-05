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
public class HuangGuaItem extends Item {

    public HuangGuaItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getHuangGuaTooltip());
    }

    private static LinkedList<Component> getHuangGuaTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【黄瓜】§r"));
        tooltip.add(Component.literal("§7食用后恢复§63点§7饥饿值，清脆爽口§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：印度北部§r"));
        tooltip.add(Component.literal("§e 传入：汉代张骞通西域时引入§r"));
        tooltip.add(Component.literal("§e 特点：水分丰富，夏季常见蔬菜§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"青瓜如玉，夏日甘露\" —— 黄瓜因其清热解暑的功效而深受喜爱§r"));
        return tooltip;
    }
}

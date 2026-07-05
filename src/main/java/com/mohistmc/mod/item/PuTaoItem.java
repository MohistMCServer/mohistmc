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
public class PuTaoItem extends Item {

    public PuTaoItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getPuTaoTooltip());
    }

    private static LinkedList<Component> getPuTaoTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【葡萄】§r"));
        tooltip.add(Component.literal("§7食用后恢复§64点§7饥饿值，富含糖分§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：西亚地区§r"));
        tooltip.add(Component.literal("§e 传入：汉代张骞通西域时引入§r"));
        tooltip.add(Component.literal("§e 特点：果实成串，可鲜食或酿酒§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"葡萄美酒夜光杯\" —— 葡萄自古就是重要的水果和酿酒原料§r"));
        return tooltip;
    }
}

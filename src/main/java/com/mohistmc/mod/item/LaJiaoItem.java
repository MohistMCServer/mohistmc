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
 * @date 2025/11/19 20:00
 */
public class LaJiaoItem extends Item {

    public LaJiaoItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getLaJiaoTooltip());
    }

    private static LinkedList<Component> getLaJiaoTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【辣椒】§r"));
        tooltip.add(Component.literal("§7食用后恢复§62点§7饥饿值，具有刺激性§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：美洲中南部§r"));
        tooltip.add(Component.literal("§e 传入：明代后期经海路§r"));
        tooltip.add(Component.literal("§e 传播：从沿海传入内陆，成为重要调料§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"色如珊瑚，味甚辣\" —— 辣椒虽小，却丰富了中华饮食文化§r"));
        return tooltip;
    }

}
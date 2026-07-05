package com.mohistmc.mod.utils;

import java.util.LinkedList;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

/**
 * @author Mgazul
 * @date 2026/1/5 23:15
 */
public class TooltipHelper {
    public static void addShiftTooltip(Consumer<Component> tooltipComponents, TooltipFlag flag, LinkedList<Component> detailComponents) {
        if (flag.hasShiftDown()) {
            detailComponents.forEach(tooltipComponents);
        } else {
            tooltipComponents.accept(Component.literal("§7按住§6Shift§7查看详细信息§r"));
        }
    }
}

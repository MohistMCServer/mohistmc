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
public class QieZiItem extends Item {

    public QieZiItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getQieZiTooltip());
    }

    private static LinkedList<Component> getQieZiTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【茄子】§r"));
        tooltip.add(Component.literal("§7食用后恢复§63点§7饥饿值，可多种烹饪方式§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：印度一带§r"));
        tooltip.add(Component.literal("§e 传入：隋唐时期传入中国§r"));
        tooltip.add(Component.literal("§e 特点：果实紫黑，营养丰富§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"落苏紫玉\" —— 茄子因其紫色外皮和丰富营养而备受喜爱§r"));
        return tooltip;
    }
}

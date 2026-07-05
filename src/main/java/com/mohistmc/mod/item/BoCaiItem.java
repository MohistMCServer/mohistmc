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
 * @date 2025/11/19 15:32
 */
public class BoCaiItem extends Item {

    public BoCaiItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getTooltip());
    }

    private static LinkedList<Component> getTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【菠菜】§r"));
        tooltip.add(Component.literal("§7食用后恢复§64点§7饥饿值，富含铁质§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：波斯（今伊朗一带）§r"));
        tooltip.add(Component.literal("§e 传入：唐代经丝绸之路§r"));
        tooltip.add(Component.literal("§e 特点：营养丰富，富含铁和维生素§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"红嘴绿鹦哥\" —— 菠菜因营养丰富被誉为\"营养模范\"§r"));
        return tooltip;
    }
}

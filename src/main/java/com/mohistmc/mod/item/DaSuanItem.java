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
public class DaSuanItem extends Item {

    public DaSuanItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getDaSuanTooltip());
    }

    private static LinkedList<Component> getDaSuanTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【大蒜】§r"));
        tooltip.add(Component.literal("§7食用后恢复§62点§7饥饿值，具有杀菌功效§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：中亚地区§r"));
        tooltip.add(Component.literal("§e 传入：汉代张骞通西域时引入§r"));
        tooltip.add(Component.literal("§e 特点：味道辛辣，具有药用价值§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"葫者，荤菜也\" —— 大蒜自古就是重要的调味和药用作物§r"));
        return tooltip;
    }
}

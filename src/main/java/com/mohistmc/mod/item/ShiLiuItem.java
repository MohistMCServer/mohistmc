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
public class ShiLiuItem extends Item {

    public ShiLiuItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getShiLiuTooltip());
    }

    private static LinkedList<Component> getShiLiuTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【石榴】§r"));
        tooltip.add(Component.literal("§7食用后恢复§64点§7饥饿值，富含维生素§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：伊朗一带§r"));
        tooltip.add(Component.literal("§e 传入：汉代张骞通西域时引入§r"));
        tooltip.add(Component.literal("§e 特点：果实多籽，象征多子多福§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"榴开百子\" —— 石榴因其多籽而象征着多子多福的美好寓意§r"));
        return tooltip;
    }
}

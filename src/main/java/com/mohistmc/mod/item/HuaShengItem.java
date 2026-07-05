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
 * @date 2025/11/19 19:40
 */
public class HuaShengItem extends Item {

    public HuaShengItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getHuaShengTooltip());
    }

    private static LinkedList<Component> getHuaShengTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【花生】§r"));
        tooltip.add(Component.literal("§7食用后恢复§64点§7饥饿值，富含油脂和蛋白质§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：南美洲§r"));
        tooltip.add(Component.literal("§e 传入：明代后期§r"));
        tooltip.add(Component.literal("§e 引种：经海路传入福建、广东等地§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"落地即生，自能结实\" —— 花生因其独特的地上开花地下结果而得名§r"));
        return tooltip;
    }


}
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
public class YuMiItem extends Item {

    public YuMiItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getYuMiTooltip());
    }

    private static LinkedList<Component> getYuMiTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【玉米】§r"));
        tooltip.add(Component.literal("§7食用后恢复§66点§7饥饿值，可进一步加工§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：美洲墨西哥§r"));
        tooltip.add(Component.literal("§e 传入：明代中后期§r"));
        tooltip.add(Component.literal("§e 引种：海路至闽浙，陆路经滇缅、西域§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"种一收千，其利甚大\" —— 此穗虽小，却托起了盛世人口之基§r"));
        return tooltip;
    }
}

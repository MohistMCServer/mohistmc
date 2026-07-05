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
 * @date 2026/1/5 23:22
 */
public class JueItem extends Item {

    public JueItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getJueTooltip());
    }

    private static LinkedList<Component> getJueTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【蕨】§r"));
        tooltip.add(Component.literal("§7食用后恢复§63点§7饥饿值，古代救荒植物§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：世界各地均有分布§r"));
        tooltip.add(Component.literal("§e 历史：古代重要野菜，饥荒时救命食物§r"));
        tooltip.add(Component.literal("§e 特点：嫩芽可食，需充分加热处理§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"蕨菜初生，春意盎然\" —— 蕨类植物是古代重要的野菜资源§r"));
        return tooltip;
    }
}

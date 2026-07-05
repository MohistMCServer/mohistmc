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
public class BoLuoItem extends Item {

    public BoLuoItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getBoLuoTooltip());
    }

    private static LinkedList<Component> getBoLuoTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【菠萝】§r"));
        tooltip.add(Component.literal("§7食用后恢复§64点§7饥饿值，富含维生素C§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：南美洲巴西一带§r"));
        tooltip.add(Component.literal("§e 传入：明代后期经菲律宾传入§r"));
        tooltip.add(Component.literal("§e 特点：果肉香甜，富含菠萝蛋白酶§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"黄瓦千钉重\" —— 菠萝因其独特的外观和香甜的果肉而深受喜爱§r"));
        return tooltip;
    }
}

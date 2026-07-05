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
public class QinCaiItem extends Item {

    public QinCaiItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getQinCaiTooltip());
    }

    private static LinkedList<Component> getQinCaiTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【芹菜】§r"));
        tooltip.add(Component.literal("§7食用后恢复§63点§7饥饿值，富含纤维§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：地中海地区§r"));
        tooltip.add(Component.literal("§e 传入：汉代已有记载§r"));
        tooltip.add(Component.literal("§e 特点：茎叶可食，具有特殊香味§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"芹菜清香，养生佳品\" —— 芹菜因其独特的香味和营养价值而备受推崇§r"));
        return tooltip;
    }
}

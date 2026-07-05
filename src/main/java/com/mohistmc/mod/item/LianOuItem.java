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
public class LianOuItem extends Item {

    public LianOuItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getLianOuTooltip());
    }

    private static LinkedList<Component> getLianOuTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【莲藕】§r"));
        tooltip.add(Component.literal("§7食用后恢复§64点§7饥饿值，清脆甘甜§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：亚洲地区§r"));
        tooltip.add(Component.literal("§e 栽培：中国自古栽培，历史悠久§r"));
        tooltip.add(Component.literal("§e 特点：根茎肥大，富含淀粉和维生素§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"出淤泥而不染\" —— 莲藕生长于淤泥中，却洁白如玉，营养丰富§r"));
        return tooltip;
    }
}

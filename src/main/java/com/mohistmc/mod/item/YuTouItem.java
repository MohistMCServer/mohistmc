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
public class YuTouItem extends Item {

    public YuTouItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getYuTouTooltip());
    }

    private static LinkedList<Component> getYuTouTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【芋头】§r"));
        tooltip.add(Component.literal("§7食用后恢复§65点§7饥饿值，富含淀粉§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：亚洲热带地区§r"));
        tooltip.add(Component.literal("§e 栽培：中国南方地区长期栽培§r"));
        tooltip.add(Component.literal("§e 特点：地下块茎，可作主食或菜肴§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"芋头软糯\" —— 芋头因其软糯口感和丰富营养而广受欢迎§r"));
        return tooltip;
    }
}

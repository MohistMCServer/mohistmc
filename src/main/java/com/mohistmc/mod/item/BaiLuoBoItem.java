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
public class BaiLuoBoItem extends Item {

    public BaiLuoBoItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getBaiLuoBoTooltip());
    }

    private static LinkedList<Component> getBaiLuoBoTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【白萝卜】§r"));
        tooltip.add(Component.literal("§7食用后恢复§63点§7饥饿值，可生食或烹饪§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：亚洲西部§r"));
        tooltip.add(Component.literal("§e 传入：汉代已有种植§r"));
        tooltip.add(Component.literal("§e 特点：根茎肥大，富含维生素C§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"冬月萝卜赛牛羊\" —— 白萝卜在寒冬时节为人们提供重要营养§r"));
        return tooltip;
    }
}

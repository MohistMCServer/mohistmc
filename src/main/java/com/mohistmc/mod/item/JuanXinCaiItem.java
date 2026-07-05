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
public class JuanXinCaiItem extends Item {

    public JuanXinCaiItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        // 使用通用方法处理Shift显示逻辑
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getJuanXinCaiTooltip());
    }

    private static LinkedList<Component> getJuanXinCaiTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【卷心菜】§r"));
        tooltip.add(Component.literal("§7食用后恢复§64点§7饥饿值，富含维生素C§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：地中海沿岸§r"));
        tooltip.add(Component.literal("§e 传入：明代后期经欧洲传入§r"));
        tooltip.add(Component.literal("§e 特点：叶片紧密包裹，耐储存§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"层层包裹如宝莲\" —— 卷心菜因其独特的叶球结构而得名§r"));
        return tooltip;
    }
}

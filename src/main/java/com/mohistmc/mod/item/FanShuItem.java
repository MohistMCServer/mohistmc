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
public class FanShuItem extends Item {

    public FanShuItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        TooltipHelper.addShiftTooltip(tooltipComponents, flag, getTooltip());
    }

    private LinkedList<Component> getTooltip() {
        LinkedList<Component> tooltip = new LinkedList<>();
        tooltip.add(Component.literal("§f【番薯】§r"));
        tooltip.add(Component.literal("§7食用后恢复§66点§7饥饿值，耐储存§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§6 源流考§r"));
        tooltip.add(Component.literal("§e 原产：美洲中南部§r"));
        tooltip.add(Component.literal("§e 传入：明代后期经菲律宾传入§r"));
        tooltip.add(Component.literal("§e 引种：陈振龙冒死携薯藤归国§r"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b\"番薯上市，饿死一半\" —— 清代饥荒因之缓解§r"));
        return tooltip;
    }
}

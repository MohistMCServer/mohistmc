package com.mohistmc.mod.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.mod.client.EnchantmentDescriptions;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/4/15 22:38
 */
@Mixin(ItemEnchantments.class)
public class MixinItemEnchants
{
    @Inject(method = "addToTooltip", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 0, shift = At.Shift.AFTER))
    public void sortedEnchantment(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components, CallbackInfo cbi, @Local(name = "enchantment") Holder<Enchantment> enchantment, @Local(name = "level") int level) {
        EnchantmentDescriptions.insertDescriptions(enchantment, level, consumer);
    }

    @Inject(method = "addToTooltip", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 1, shift = At.Shift.AFTER))
    public void unsortedEnchantment(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag,
                                    DataComponentGetter components,
                                    CallbackInfo cbi,
                                    @Local(name = "entry") Object2IntMap.Entry<Holder<Enchantment>> entry,
                                    @Local(name = "enchantment") Holder<Enchantment> enchantment) {
        EnchantmentDescriptions.insertDescriptions(enchantment, entry.getIntValue(), consumer);
    }
}

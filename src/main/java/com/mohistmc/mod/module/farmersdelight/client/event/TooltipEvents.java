package com.mohistmc.mod.module.farmersdelight.client.event;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.Configuration;
import com.mohistmc.mod.module.farmersdelight.common.FoodValues;
import com.mohistmc.mod.module.farmersdelight.common.utility.TextUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = FarmersDelight.MODID, value = Dist.CLIENT)
public class TooltipEvents
{
	@SubscribeEvent
	public static void addTooltipToVanillaSoups(ItemTooltipEvent event) {
		Item food = event.getItemStack().getItem();

		if (food.equals(Items.PUMPKIN_PIE)) {
			event.getToolTip().add(Configuration.ENABLE_PUMPKIN_PIE_SNEAK_TO_PLACE.get() ? TextUtils.PLACEABLE_SNEAKING : TextUtils.PLACEABLE);
		}

		if (!Configuration.ENABLE_VANILLA_SOUP_EXTRA_EFFECTS.get() || !Configuration.ENABLE_FOOD_EFFECT_TOOLTIP.get()) {
			return;
		}

		MobEffectInstance soupEffect = FoodValues.VANILLA_SOUP_EFFECTS.get(food);

		if (soupEffect != null) {
			TextUtils.addEffectTooltip(soupEffect, event.getToolTip()::add, 1.0F, event.getContext().tickRate());
		}
	}
}

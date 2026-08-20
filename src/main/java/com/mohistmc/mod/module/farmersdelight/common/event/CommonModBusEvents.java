package com.mohistmc.mod.module.farmersdelight.common.event;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.FoodValues;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

@EventBusSubscriber(modid = FarmersDelight.MODID)
public class CommonModBusEvents
{
	@SubscribeEvent
	public static void onModifyDefaultComponents(ModifyDefaultComponentsEvent event) {
		if (DatagenModLoader.isRunningDataGen()) {
			return;
		}
		List.of("minecraft:mushroom_stew", "minecraft:beetroot_soup", "minecraft:rabbit_stew").forEach((key) -> {
			BuiltInRegistries.ITEM.get(Identifier.parse(key)).ifPresent(item -> {
				event.modify(item.value(), (builder) -> builder.set(DataComponents.MAX_STACK_SIZE, 16));
			});
		});
		event.modify(Items.RABBIT_STEW, (builder) -> builder
			.set(DataComponents.FOOD, FoodValues.RABBIT_STEW_BUFF)
			.set(DataComponents.CONSUMABLE, FoodValues.consumable(FoodValues.RABBIT_STEW_BUFF)));
	}
}

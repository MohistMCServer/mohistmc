package com.mohistmc.mod.module.farmersdelight.common;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModItems;

public class CommonSetup
{
	public static void init(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			registerDispenserBehaviors();
			registerItemSetAdditions();
		});
	}

	public static void registerDispenserBehaviors() {
		DispenserBlock.registerProjectileBehavior(ModItems.ROTTEN_TOMATO.get());
	}

	public static void registerItemSetAdditions() {
		HashMap<Item, Integer> newFoodPoints = new HashMap<>();
		newFoodPoints.put(ModItems.CABBAGE.get(), 1);
		newFoodPoints.put(ModItems.TOMATO.get(), 1);
		newFoodPoints.put(ModItems.ONION.get(), 1);
		newFoodPoints.put(ModItems.RICE.get(), 2);
		newFoodPoints.putAll(Villager.FOOD_POINTS);

		Villager.FOOD_POINTS = ImmutableMap.copyOf(newFoodPoints);
	}
}

package com.mohistmc.mod.module.farmersdelight.common.utility;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CookingPotRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CuttingBoardRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

public class RecipeUtils
{
	// Copyright (c) 2014-2015 mezz
	public static ItemStack getResultItem(Recipe<?> recipe) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level != null && !recipe.display().isEmpty()) {
			return recipe.display().getFirst().result().resolveForFirstStack(SlotDisplayContext.fromLevel(level));
		}

		RegistryAccess registryAccess = level != null ? level.registryAccess() : RegistryAccess.EMPTY;
		if (recipe instanceof CookingPotRecipe cookingPotRecipe) {
			return cookingPotRecipe.getResultItem(registryAccess);
		}
		if (recipe instanceof CuttingBoardRecipe cuttingBoardRecipe) {
			return cuttingBoardRecipe.getResultItem(registryAccess);
		}
		return ItemStack.EMPTY;
	}

	public static Identifier FDLocation(String name) {
		return Identifier.fromNamespaceAndPath(FarmersDelight.MODID, name);
	}
}

package com.mohistmc.mod.module.farmersdelight.integration.jei;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CookingPotRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CuttingBoardRecipe;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModRecipeTypes;
import java.util.List;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;

// Mirrors the data source used by the Create JEI plugin: recipes are read from the
// server-synced RecipeMap instead of re-parsing recipe JSON files on the client.
public class FDRecipes
{
	public static List<RecipeHolder<CookingPotRecipe>> getCookingPotRecipes(RecipeMap preparedRecipes) {
		List<RecipeHolder<CookingPotRecipe>> recipes = preparedRecipes.byType(ModRecipeTypes.COOKING.get()).stream().toList();
		FarmersDelight.LOGGER.info("[FD-JEI] cooking recipes from synced RecipeMap: {} (total synced recipes: {})",
				recipes.size(), preparedRecipes.values().size());
		return recipes;
	}

	public static List<RecipeHolder<CuttingBoardRecipe>> getCuttingBoardRecipes(RecipeMap preparedRecipes) {
		List<RecipeHolder<CuttingBoardRecipe>> recipes = preparedRecipes.byType(ModRecipeTypes.CUTTING.get()).stream().toList();
		FarmersDelight.LOGGER.info("[FD-JEI] cutting recipes from synced RecipeMap: {} (total synced recipes: {})",
				recipes.size(), preparedRecipes.values().size());
		return recipes;
	}
}

package com.mohistmc.mod.module.farmersdelight.data;

import com.mohistmc.mod.module.farmersdelight.data.recipe.CookingRecipes;
import com.mohistmc.mod.module.farmersdelight.data.recipe.CraftingRecipes;
import com.mohistmc.mod.module.farmersdelight.data.recipe.CuttingRecipes;
import com.mohistmc.mod.module.farmersdelight.data.recipe.SmeltingRecipes;
import java.util.concurrent.CompletableFuture;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

@ParametersAreNonnullByDefault
public class Recipes extends RecipeProvider
{
	public Recipes(HolderLookup.Provider registries, RecipeOutput output) {
		super(registries, output);
	}

	@Override
	protected void buildRecipes() {
		CraftingRecipes.register(output, items);
		SmeltingRecipes.register(output);
		CookingRecipes.register(registries, output);
		CuttingRecipes.register(registries, output);
	}

	public static class Runner extends RecipeProvider.Runner {

		public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
			super(output, registries);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
			return new Recipes(registries, output);
		}

		@Override
		public String getName() {
			return "Farmer's Delight recipes";
		}
	}
}

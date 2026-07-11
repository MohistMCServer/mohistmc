package com.mohistmc.mod.module.farmersdelight.integration.jei;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CookingPotRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CuttingBoardRecipe;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModItems;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModRecipeTypes;
import com.mohistmc.mod.module.farmersdelight.common.utility.RecipeUtils;

public class FDRecipes
{
	// TODO this absolutely sucks but might be necessary for JEI recipe registration
	public static MinecraftServer SERVER;
	private final RecipeManager recipeManager;

	public FDRecipes() {
		if (SERVER != null) {
			this.recipeManager = SERVER.getRecipeManager();
		} else {
			throw new NullPointerException("Minecraft level must not be null.");
		}
	}

	public List<RecipeHolder<CookingPotRecipe>> getCookingPotRecipes() {
		return recipeManager.getRecipes().stream()
			.filter(r -> r.value().getType() == ModRecipeTypes.COOKING.get())
			.map(r -> (RecipeHolder<CookingPotRecipe>) (r) )
			.toList();
	}

	public List<RecipeHolder<CuttingBoardRecipe>> getCuttingBoardRecipes() {
		return recipeManager.getRecipes().stream()
			.filter(r -> r.value().getType() == ModRecipeTypes.CUTTING.get())
			.map(r -> (RecipeHolder<CuttingBoardRecipe>) (r) )
			.toList();
	}

	public List<RecipeHolder<CraftingRecipe>> getSpecialCraftingRecipes() {
		List<RecipeHolder<CraftingRecipe>> recipes = Lists.newArrayList();

		addValidatedSpecialRecipe(recipes, "wheat_dough_from_water", "fd_dough",
				NonNullList.of(
						Ingredient.of(),
						Ingredient.of(Items.WHEAT),
						Ingredient.of(Items.WATER_BUCKET)
				),
				ModItems.WHEAT_DOUGH.get()
		);

		return recipes;
	}

	public void addValidatedSpecialRecipe(List<RecipeHolder<CraftingRecipe>> recipeList, String recipeId, String group, NonNullList<Ingredient> inputs, ItemLike output) {
		Optional<RecipeHolder<?>> specialRecipe = recipeManager.byKey(ResourceKey.create(Registries.RECIPE, RecipeUtils.FDLocation(recipeId)));

		specialRecipe.ifPresent((recipe) -> {
			ShapelessRecipe shapeless = new ShapelessRecipe(
				new Recipe.CommonInfo(false),
				new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, group),
				ItemStackTemplate.fromNonEmptyStack(new ItemStack(output)),
				inputs
			);
			recipeList.add(new RecipeHolder<>(specialRecipe.get().id(), shapeless));
		});
	}
}

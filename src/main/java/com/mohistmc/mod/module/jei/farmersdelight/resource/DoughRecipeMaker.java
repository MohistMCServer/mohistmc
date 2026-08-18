package com.mohistmc.mod.module.jei.farmersdelight.resource;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModItems;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class DoughRecipeMaker
{
	public static List<RecipeHolder<CraftingRecipe>> createRecipe() {
		NonNullList<Ingredient> inputs = NonNullList.of(
                // This was an empty stack, but JEI complained, and it isn't visible anyway..
				Ingredient.of(Items.WHEAT),
				Ingredient.of(Items.WHEAT),
				Ingredient.of(Items.WATER_BUCKET)
		);

		ItemStackTemplate output = new ItemStackTemplate(ModItems.WHEAT_DOUGH.get());
		String path = FarmersDelight.MODID + ".dough";

		Identifier id = FarmersDelight.id("wheat_dough_from_water");
		return List.of(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, id), new ShapelessRecipe(
				new Recipe.CommonInfo(false),
				new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, path),
				output, inputs)));
	}
}

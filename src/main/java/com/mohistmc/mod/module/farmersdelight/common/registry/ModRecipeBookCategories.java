package com.mohistmc.mod.module.farmersdelight.common.registry;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeBookCategories
{
	public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES =
			DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, FarmersDelight.MODID);

	public static final Supplier<RecipeBookCategory> COOKING_MEALS = register("cooking_meals");
	public static final Supplier<RecipeBookCategory> COOKING_DRINKS = register("cooking_drinks");
	public static final Supplier<RecipeBookCategory> COOKING_MISC = register("cooking_misc");

	private static Supplier<RecipeBookCategory> register(String name) {
		return RECIPE_BOOK_CATEGORIES.register(name, RecipeBookCategory::new);
	}
}

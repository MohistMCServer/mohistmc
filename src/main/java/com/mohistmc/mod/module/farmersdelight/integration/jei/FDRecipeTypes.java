package com.mohistmc.mod.module.farmersdelight.integration.jei;

import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.world.item.crafting.RecipeHolder;
import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CookingPotRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CuttingBoardRecipe;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModRecipeTypes;
import com.mohistmc.mod.module.farmersdelight.integration.jei.resource.DecompositionDummy;

public final class FDRecipeTypes
{
	public static final IRecipeType<RecipeHolder<CookingPotRecipe>> COOKING = IRecipeType.create(ModRecipeTypes.COOKING.get());
	public static final IRecipeType<RecipeHolder<CuttingBoardRecipe>> CUTTING = IRecipeType.create(ModRecipeTypes.CUTTING.get());
	public static final IRecipeType<DecompositionDummy> DECOMPOSITION = IRecipeType.create(FarmersDelight.MODID, "decomposition", DecompositionDummy.class);
}

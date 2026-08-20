package com.mohistmc.mod.module.jei.farmersdelight;

import com.mohistmc.mod.module.farmersdelight.common.crafting.CookingPotRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CuttingBoardRecipe;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModRecipeTypes;
import java.util.List;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import org.jspecify.annotations.Nullable;

/**
 * @author Mgazul
 * @date 2026/8/19 07:58
 */
@EventBusSubscriber
public class FDRecipes
{
    private static @Nullable RecipeMap recipeMap;

    public FDRecipes() {
        if (recipeMap == null) {
            throw new NullPointerException("Minecraft level must not be null.");
        }
    }

    @SubscribeEvent
    public static void receiveRecipes(RecipesReceivedEvent event) {
        FDRecipes.recipeMap = event.getRecipeMap();
    }

    public List<RecipeHolder<CookingPotRecipe>> getCookingPotRecipes() {
        return recipeMap.byType(ModRecipeTypes.COOKING.get()).stream().toList();
    }

    public List<RecipeHolder<CuttingBoardRecipe>> getCuttingBoardRecipes() {
        return recipeMap.byType(ModRecipeTypes.CUTTING.get()).stream().toList();
    }
}

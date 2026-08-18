package com.mohistmc.mod.module.jei.category;

import com.mohistmc.mod.module.create.AllItems;
import com.mohistmc.mod.module.create.AllRecipeTypes;
import com.mohistmc.mod.module.jei.JeiClientPlugin;
import com.mohistmc.mod.module.jei.renderer.TwoIconRenderer;
import com.mohistmc.mod.module.create.client.foundation.gui.render.MixingBasinRenderState;
import com.mohistmc.mod.module.create.client.foundation.utility.CreateLang;
import com.mohistmc.mod.module.create.content.kinetics.mixer.MixingRecipe;
import com.mohistmc.mod.module.create.content.processing.recipe.ProcessingOutput;
import com.mohistmc.mod.module.create.infrastructure.fluids.FluidStack;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import org.joml.Matrix3x2f;

public class MixingCategory extends BasinCategory<MixingRecipe> {
    public static List<RecipeHolder<MixingRecipe>> getRecipes(RecipeMap preparedRecipes) {
        return preparedRecipes.byType(AllRecipeTypes.MIXING.get()).stream().toList();
    }

    @Override
    public IRecipeType<RecipeHolder<MixingRecipe>> getRecipeType() {
        return JeiClientPlugin.MIXING;
    }

    @Override
    public Component getTitle() {
        return CreateLang.translateDirect("recipe.mixing");
    }

    @Override
    public IDrawable getIcon() {
        return new TwoIconRenderer(AllItems.MECHANICAL_MIXER, AllItems.BASIN);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<MixingRecipe> entry, IFocusGroup focuses) {
        MixingRecipe recipe = entry.value();
        addIngredientSlots(builder, recipe);
        List<ProcessingOutput> results = recipe.results();
        List<FluidStack> fluidResults = recipe.fluidResults();
        int resultSize = results.size();
        int size = resultSize + fluidResults.size();
        boolean isOddSize = size % 2 != 0;
        int end = size - 1;
        int y = size <= 4 ? 51 : 60;
        addResultSlots(builder, results, 0, end, y, isOddSize);
        addFluidResultSlots(builder, fluidResults, resultSize, end, y, isOddSize);
        addHeatSlots(builder, recipe);
    }

    @Override
    public void draw(
        RecipeHolder<MixingRecipe> entry,
        IRecipeSlotsView recipeSlotsView,
        GuiGraphicsExtractor graphics,
        double mouseX,
        double mouseY
    ) {
        MixingRecipe recipe = entry.value();
        drawBackground(recipe, graphics, recipe.results().size() + recipe.fluidResults().size());
        graphics.guiRenderState.addPicturesInPictureState(new MixingBasinRenderState(
            new Matrix3x2f(graphics.pose()),
            91,
            -5
        ));
    }
}

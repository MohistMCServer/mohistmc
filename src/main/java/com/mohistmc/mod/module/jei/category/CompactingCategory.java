package com.mohistmc.mod.module.jei.category;

import com.mohistmc.mod.module.create.AllItems;
import com.mohistmc.mod.module.create.AllRecipeTypes;
import com.mohistmc.mod.module.create.client.foundation.gui.render.PressBasinRenderState;
import com.mohistmc.mod.module.create.client.foundation.utility.CreateLang;
import com.mohistmc.mod.module.create.content.kinetics.mixer.CompactingRecipe;
import com.mohistmc.mod.module.create.content.processing.recipe.ProcessingOutput;
import com.mohistmc.mod.module.jei.JeiClientPlugin;
import com.mohistmc.mod.module.jei.renderer.TwoIconRenderer;
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

public class CompactingCategory extends BasinCategory<CompactingRecipe> {
    public static List<RecipeHolder<CompactingRecipe>> getRecipes(RecipeMap preparedRecipes) {
        return preparedRecipes.byType(AllRecipeTypes.COMPACTING.get()).stream().toList();
    }

    @Override
    public IRecipeType<RecipeHolder<CompactingRecipe>> getRecipeType() {
        return JeiClientPlugin.PACKING;
    }

    @Override
    public Component getTitle() {
        return CreateLang.translateDirect("recipe.packing");
    }

    @Override
    public IDrawable getIcon() {
        return new TwoIconRenderer(AllItems.MECHANICAL_PRESS, AllItems.BASIN);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CompactingRecipe> entry, IFocusGroup iFocusGroup) {
        CompactingRecipe recipe = entry.value();
        addIngredientSlots(builder, recipe);
        List<ProcessingOutput> results = recipe.results();
        int size = results.size();
        addResultSlots(builder, results, 0, size - 1, 51, size % 2 != 0);
        addHeatSlots(builder, recipe);
    }

    @Override
    public void draw(
        RecipeHolder<CompactingRecipe> entry,
        IRecipeSlotsView recipeSlotsView,
        GuiGraphicsExtractor graphics,
        double mouseX,
        double mouseY
    ) {
        CompactingRecipe recipe = entry.value();
        drawBackground(recipe, graphics, recipe.results().size());
        graphics.guiRenderState.addPicturesInPictureState(new PressBasinRenderState(
            new Matrix3x2f(graphics.pose()),
            91,
            -5
        ));
    }
}

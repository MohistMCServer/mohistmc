package com.mohistmc.mod.module.create.client.compat.jei.category;

import com.mohistmc.mod.module.create.AllItems;
import com.mohistmc.mod.module.create.AllRecipeTypes;
import com.mohistmc.mod.module.create.client.compat.jei.CreateCategory;
import com.mohistmc.mod.module.create.client.compat.jei.JeiClientPlugin;
import com.mohistmc.mod.module.create.client.compat.jei.renderer.TwoIconRenderer;
import com.mohistmc.mod.module.create.client.foundation.gui.AllGuiTextures;
import com.mohistmc.mod.module.create.client.foundation.gui.render.PressRenderState;
import com.mohistmc.mod.module.create.client.foundation.utility.CreateLang;
import com.mohistmc.mod.module.create.content.kinetics.press.PressingRecipe;
import com.mohistmc.mod.module.create.content.processing.recipe.ProcessingOutput;
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

public class PressingCategory extends CreateCategory<RecipeHolder<PressingRecipe>> {
    public static List<RecipeHolder<PressingRecipe>> getRecipes(RecipeMap preparedRecipes) {
        return preparedRecipes.byType(AllRecipeTypes.PRESSING.get()).stream().toList();
    }

    @Override
    public IRecipeType<RecipeHolder<PressingRecipe>> getRecipeType() {
        return JeiClientPlugin.PRESSING;
    }

    @Override
    public Component getTitle() {
        return CreateLang.translateDirect("recipe.pressing");
    }

    @Override
    public IDrawable getIcon() {
        return new TwoIconRenderer(AllItems.MECHANICAL_PRESS, AllItems.IRON_SHEET);
    }

    @Override
    public int getHeight() {
        return 70;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<PressingRecipe> entry, IFocusGroup focuses) {
        PressingRecipe recipe = entry.value();
        builder.addInputSlot(27, 51).setBackground(SLOT, -1, -1).add(recipe.ingredient());
        List<ProcessingOutput> results = recipe.results();
        for (int i = 0, size = results.size(); i < size; i++) {
            addChanceSlot(builder, 131 + 19 * i, 51, results.get(i));
        }
    }

    @Override
    public void draw(
        RecipeHolder<PressingRecipe> entry,
        IRecipeSlotsView recipeSlotsView,
        GuiGraphicsExtractor graphics,
        double mouseX,
        double mouseY
    ) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 61, 41);
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 54);
        graphics.guiRenderState.addPicturesInPictureState(new PressRenderState(
            new Matrix3x2f(graphics.pose()),
            73,
            -16
        ));
    }
}

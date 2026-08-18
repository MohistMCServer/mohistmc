package com.mohistmc.mod.module.jei.category;

import com.mohistmc.mod.module.create.AllItems;
import com.mohistmc.mod.module.create.AllRecipeTypes;
import com.mohistmc.mod.module.jei.CreateCategory;
import com.mohistmc.mod.module.jei.JeiClientPlugin;
import com.mohistmc.mod.module.jei.renderer.TwoIconRenderer;
import com.mohistmc.mod.module.create.client.foundation.gui.AllGuiTextures;
import com.mohistmc.mod.module.create.client.foundation.gui.render.CrushWheelRenderState;
import com.mohistmc.mod.module.create.client.foundation.utility.CreateLang;
import com.mohistmc.mod.module.create.content.kinetics.crusher.CrushingRecipe;
import com.mohistmc.mod.module.create.content.kinetics.millstone.MillingRecipe;
import com.mohistmc.mod.module.create.content.processing.recipe.ProcessingOutput;
import com.mohistmc.mod.module.create.foundation.recipe.CreateSingleStackRollableRecipe;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import org.joml.Matrix3x2f;

public class CrushingCategory extends CreateCategory<RecipeHolder<? extends CreateSingleStackRollableRecipe>> {
    public static List<RecipeHolder<? extends CreateSingleStackRollableRecipe>> getRecipes(RecipeMap preparedRecipes) {
        Collection<RecipeHolder<CrushingRecipe>> crushingRecipes = preparedRecipes.byType(AllRecipeTypes.CRUSHING.get());
        List<RecipeHolder<? extends CreateSingleStackRollableRecipe>> recipes = new ArrayList<>(crushingRecipes);
        for (RecipeHolder<MillingRecipe> entry : preparedRecipes.byType(AllRecipeTypes.MILLING.get())) {
            MillingRecipe recipe = entry.value();
            Ingredient ingredient = recipe.ingredient();
            ItemStack firstInput = ingredient.values.stream().findFirst().map(item -> item.value().getDefaultInstance())
                .orElse(ItemStack.EMPTY);
            if (!firstInput.isEmpty() && crushingRecipes.stream()
                .anyMatch(e -> e.value().ingredient().test(firstInput))) {
                continue;
            }
            recipes.add(entry);
        }
        return recipes;
    }

    @Override
    public IRecipeType<RecipeHolder<? extends CreateSingleStackRollableRecipe>> getRecipeType() {
        return JeiClientPlugin.CRUSHING;
    }

    @Override
    public Component getTitle() {
        return CreateLang.translateDirect("recipe.crushing");
    }

    @Override
    public IDrawable getIcon() {
        return new TwoIconRenderer(AllItems.CRUSHING_WHEEL, AllItems.CRUSHED_GOLD);
    }

    @Override
    public int getHeight() {
        return 100;
    }

    @Override
    public void setRecipe(
        IRecipeLayoutBuilder builder,
        RecipeHolder<? extends CreateSingleStackRollableRecipe> entry,
        IFocusGroup focuses
    ) {
        CreateSingleStackRollableRecipe recipe = entry.value();
        builder.addInputSlot(51, 3).setBackground(SLOT, -1, -1).add(recipe.ingredient());
        List<ProcessingOutput> results = recipe.results();
        for (int i = 0, size = results.size(), start = (179 - 19 * size) / 2 + 3; i < size; i++) {
            addChanceSlot(builder, start + i * 19, 83, results.get(i));
        }
    }

    @Override
    public void draw(
        RecipeHolder<? extends CreateSingleStackRollableRecipe> entry,
        IRecipeSlotsView recipeSlotsView,
        GuiGraphicsExtractor graphics,
        double mouseX,
        double mouseY
    ) {
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 72, 7);
        graphics.guiRenderState.addPicturesInPictureState(new CrushWheelRenderState(
            new Matrix3x2f(graphics.pose()),
            42,
            24
        ));
    }
}

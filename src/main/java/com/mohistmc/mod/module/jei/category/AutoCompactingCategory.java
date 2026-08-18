package com.mohistmc.mod.module.jei.category;

import com.mohistmc.mod.module.create.AllItems;
import com.mohistmc.mod.module.create.AllRecipeTypes;
import com.mohistmc.mod.module.jei.CreateCategory;
import com.mohistmc.mod.module.jei.JeiClientPlugin;
import com.mohistmc.mod.module.jei.renderer.TwoIconRenderer;
import com.mohistmc.mod.module.create.client.foundation.gui.AllGuiTextures;
import com.mohistmc.mod.module.create.client.foundation.gui.render.PressBasinRenderState;
import com.mohistmc.mod.module.create.client.foundation.utility.CreateLang;
import com.mohistmc.mod.module.create.content.kinetics.press.MechanicalPressBlockEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.joml.Matrix3x2f;

public class AutoCompactingCategory extends CreateCategory<RecipeHolder<CraftingRecipe>> {
    public static List<RecipeHolder<CraftingRecipe>> getRecipes(RecipeMap preparedRecipes) {
        List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
        for (RecipeHolder<CraftingRecipe> entry : preparedRecipes.byType(RecipeType.CRAFTING)) {
            CraftingRecipe recipe = entry.value();
            if (!MechanicalPressBlockEntity.canCompress(recipe) || AllRecipeTypes.shouldIgnoreInAutomation(entry)) {
                continue;
            }
            if (recipe instanceof ShapelessRecipe || recipe instanceof ShapedRecipe) {
                recipes.add(entry);
            }
        }
        return recipes;
    }

    @Override
    public IRecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
        return JeiClientPlugin.AUTOMATIC_PACKING;
    }

    @Override
    public Component getTitle() {
        return CreateLang.translateDirect("recipe.automatic_packing");
    }

    @Override
    public IDrawable getIcon() {
        return new TwoIconRenderer(AllItems.MECHANICAL_PRESS, Items.CRAFTING_TABLE);
    }

    @Override
    public int getHeight() {
        return 85;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CraftingRecipe> entry, IFocusGroup focuses) {
        CraftingRecipe recipe = entry.value();
        ItemStackTemplate result;
        List<Ingredient> ingredients;
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            result = shapedRecipe.result;
            ingredients = shapedRecipe.getIngredients().stream().filter(Optional::isPresent).map(Optional::get)
                .toList();
        } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            result = shapelessRecipe.result;
            ingredients = shapelessRecipe.ingredients;
        } else {
            return;
        }
        for (int i = 0, size = ingredients.size(), rows = size == 4 ? 2 : 3; i < size; i++) {
            builder.addInputSlot((rows == 2 ? 27 : 18) + i % rows * 19, 51 - i / rows * 19).setBackground(SLOT, -1, -1)
                .add(ingredients.get(i));
        }
        builder.addOutputSlot(142, 51).setBackground(SLOT, -1, -1).add(result);
    }

    @Override
    public void draw(
        RecipeHolder<CraftingRecipe> entry,
        IRecipeSlotsView recipeSlotsView,
        GuiGraphicsExtractor graphics,
        double mouseX,
        double mouseY
    ) {
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 136, 32);
        AllGuiTextures.JEI_SHADOW.render(graphics, 81, 68);
        graphics.guiRenderState.addPicturesInPictureState(new PressBasinRenderState(
            new Matrix3x2f(graphics.pose()),
            91,
            -5
        ));
    }
}

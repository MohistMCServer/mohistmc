package com.mohistmc.mod.module.jei.category;

import com.mohistmc.mod.module.create.AllItems;
import com.mohistmc.mod.module.create.AllRecipeTypes;
import com.mohistmc.mod.module.create.client.foundation.gui.AllGuiTextures;
import com.mohistmc.mod.module.create.client.foundation.gui.render.FanRenderState;
import com.mohistmc.mod.module.create.client.foundation.utility.CreateLang;
import com.mohistmc.mod.module.jei.CreateCategory;
import com.mohistmc.mod.module.jei.JeiClientPlugin;
import com.mohistmc.mod.module.jei.renderer.TwoIconRenderer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.material.Fluids;
import org.joml.Matrix3x2f;

public class FanBlastingCategory extends CreateCategory<RecipeHolder<? extends SingleItemRecipe>> {
    public static List<RecipeHolder<? extends SingleItemRecipe>> getRecipes(RecipeMap preparedRecipes) {
        List<RecipeHolder<? extends SingleItemRecipe>> recipes = new ArrayList<>();
        Collection<RecipeHolder<BlastingRecipe>> blastingRecipes = preparedRecipes.byType(RecipeType.BLASTING);
        Collection<RecipeHolder<SmokingRecipe>> smokingRecipes = preparedRecipes.byType(RecipeType.SMOKING);
        ClientLevel level = Minecraft.getInstance().level;
        for (RecipeHolder<BlastingRecipe> entry : blastingRecipes) {
            addRecipe(recipes, entry, level, null, smokingRecipes);
        }
        for (RecipeHolder<SmeltingRecipe> entry : preparedRecipes.byType(RecipeType.SMELTING)) {
            addRecipe(recipes, entry, level, blastingRecipes, smokingRecipes);
        }
        return recipes;
    }

    private static void addRecipe(
        List<RecipeHolder<? extends SingleItemRecipe>> list,
        RecipeHolder<? extends SingleItemRecipe> entry,
        ClientLevel level,
        Collection<RecipeHolder<BlastingRecipe>> blastingRecipes,
        Collection<RecipeHolder<SmokingRecipe>> smokingRecipes
    ) {
        if (!AllRecipeTypes.CAN_BE_AUTOMATED.test(entry)) {
            return;
        }
        SingleItemRecipe recipe = entry.value();
        Ingredient ingredient = recipe.input();
        ItemStack firstInput = ingredient.values.stream().findFirst().map(item -> item.value().getDefaultInstance())
            .orElse(ItemStack.EMPTY);
        if (firstInput.isEmpty()) {
            return;
        }
        SingleRecipeInput input = new SingleRecipeInput(firstInput);
        if (blastingRecipes != null) {
            Optional<RecipeHolder<BlastingRecipe>> blastingRecipe = blastingRecipes.stream()
                .filter(e -> e.value().matches(input, level)).findFirst().filter(AllRecipeTypes.CAN_BE_AUTOMATED);
            if (blastingRecipe.isPresent()) {
                return;
            }
        }
        Optional<RecipeHolder<SmokingRecipe>> smokingRecipe = smokingRecipes.stream()
            .filter(e -> e.value().matches(input, level)).findFirst().filter(AllRecipeTypes.CAN_BE_AUTOMATED);
        if (smokingRecipe.isPresent()) {
            return;
        }
        list.add(entry);
    }

    @Override
    public IRecipeType<RecipeHolder<? extends SingleItemRecipe>> getRecipeType() {
        return JeiClientPlugin.FAN_BLASTING;
    }

    @Override
    public Component getTitle() {
        return CreateLang.translateDirect("recipe.fan_blasting");
    }

    @Override
    public IDrawable getIcon() {
        return new TwoIconRenderer(AllItems.PROPELLER, Items.LAVA_BUCKET);
    }

    @Override
    public int getHeight() {
        return 72;
    }

    @Override
    public void setRecipe(
        IRecipeLayoutBuilder builder,
        RecipeHolder<? extends SingleItemRecipe> entry,
        IFocusGroup focuses
    ) {
        SingleItemRecipe recipe = entry.value();
        builder.addInputSlot(21, 48).setBackground(SLOT, -1, -1).add(recipe.input());
        builder.addOutputSlot(141, 48).setBackground(SLOT, -1, -1).add(recipe.result());
    }

    @Override
    public void draw(
        RecipeHolder<? extends SingleItemRecipe> entry,
        IRecipeSlotsView recipeSlotsView,
        GuiGraphicsExtractor graphics,
        double mouseX,
        double mouseY
    ) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 46, 27);
        AllGuiTextures.JEI_LIGHT.render(graphics, 65, 39);
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 54, 51);
        graphics.guiRenderState.addPicturesInPictureState(new FanRenderState(
            new Matrix3x2f(graphics.pose()),
            56,
            4,
            Fluids.LAVA.defaultFluidState().createLegacyBlock()
        ));
    }
}

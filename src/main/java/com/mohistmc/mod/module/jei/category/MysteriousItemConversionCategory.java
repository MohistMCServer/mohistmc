package com.mohistmc.mod.module.jei.category;

import com.mohistmc.mod.module.create.AllItems;
import com.mohistmc.mod.module.create.client.foundation.gui.AllGuiTextures;
import com.mohistmc.mod.module.create.client.foundation.utility.CreateLang;
import com.mohistmc.mod.module.jei.CreateCategory;
import com.mohistmc.mod.module.jei.JeiClientPlugin;
import com.mohistmc.mod.module.jei.display.MysteriousItemConversionDisplay;
import com.mohistmc.mod.module.jei.renderer.IconRenderer;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static com.mohistmc.mod.module.create.Create.MOD_ID;

public class MysteriousItemConversionCategory extends CreateCategory<MysteriousItemConversionDisplay> {
    public static List<MysteriousItemConversionDisplay> getRecipes() {
        return List.of(
            new MysteriousItemConversionDisplay(
                Identifier.fromNamespaceAndPath(MOD_ID, "to_blaze_burner"),
                AllItems.EMPTY_BLAZE_BURNER,
                AllItems.BLAZE_BURNER
            ), new MysteriousItemConversionDisplay(
                Identifier.fromNamespaceAndPath(MOD_ID, "to_haunted_bell"),
                AllItems.PECULIAR_BELL,
                AllItems.HAUNTED_BELL
            )
        );
    }

    @Override
    public Identifier getIdentifier(MysteriousItemConversionDisplay display) {
        return display.id();
    }

    @Override
    public IRecipeType<MysteriousItemConversionDisplay> getRecipeType() {
        return JeiClientPlugin.MYSTERY_CONVERSION;
    }

    @Override
    public Component getTitle() {
        return CreateLang.translateDirect("recipe.mystery_conversion");
    }

    @Override
    public IDrawable getIcon() {
        return new IconRenderer(AllItems.PECULIAR_BELL);
    }

    @Override
    public int getHeight() {
        return 50;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MysteriousItemConversionDisplay display, IFocusGroup focuses) {
        builder.addInputSlot(27, 17).setBackground(SLOT, -1, -1).add(display.input());
        builder.addOutputSlot(132, 17).setBackground(SLOT, -1, -1).add(display.output());
    }

    @Override
    public void draw(
        MysteriousItemConversionDisplay recipe,
        IRecipeSlotsView recipeSlotsView,
        GuiGraphicsExtractor graphics,
        double mouseX,
        double mouseY
    ) {
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 20);
        AllGuiTextures.JEI_QUESTION_MARK.render(graphics, 77, 5);
    }
}

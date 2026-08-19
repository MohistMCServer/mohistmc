package com.mohistmc.mod.module.jei.farmersdelight.category;

import com.google.common.base.Suppliers;
import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CuttingBoardRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.ingredient.ChanceResult;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModItems;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModRecipeTypes;
import com.mohistmc.mod.module.farmersdelight.common.utility.TextUtils;
import com.mohistmc.mod.module.jei.CreateCategory;
import com.mohistmc.mod.module.jei.JeiClientPlugin;
import com.mohistmc.mod.module.jei.renderer.IconRenderer;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;

// Mirrors the Create JEI category style: no-arg constructor, no IGuiHelper.
@ParametersAreNonnullByDefault
public class CuttingRecipeCategory implements IRecipeCategory<RecipeHolder<CuttingBoardRecipe>>
{
	public static final int OUTPUT_GRID_X = 76;
	public static final int OUTPUT_GRID_Y = 10;
	private static final Identifier BACKGROUND_IMAGE =
			Identifier.fromNamespaceAndPath(FarmersDelight.MODID, "textures/gui/jei/cutting_board.png");
	private final Component title;

	public CuttingRecipeCategory() {
		title = TextUtils.JEI("cutting");
	}

	@Override
	public IRecipeType<RecipeHolder<CuttingBoardRecipe>> getRecipeType() {
		return JeiClientPlugin.CUTTING;
	}

	@Override
	public Component getTitle() {
		return this.title;
	}

	@Override
	public int getWidth() {
		return 117;
	}

	@Override
	public int getHeight() {
		return 57;
	}

	@Override
	public IDrawable getIcon() {
		return new IconRenderer(ModItems.CUTTING_BOARD.get());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CuttingBoardRecipe> holder, IFocusGroup focusGroup) {
		CuttingBoardRecipe recipe = holder.value();
		// Use Create's getStacks + addItemStacks instead of .add(Ingredient): the latter triggers
		// Ingredient.display(), which crashes on CompoundIngredient in MC 26.2 (stream reused).
		Supplier<ContextMap> context = Suppliers.memoize(CreateCategory::createIngredientContext);
		builder.addSlot(RecipeIngredientRole.INPUT, 16, 8)
				.addItemStacks(CreateCategory.getStacks(recipe.getTool(), context));
		builder.addSlot(RecipeIngredientRole.INPUT, 16, 27)
				.addItemStacks(CreateCategory.getStacks(recipe.getIngredients().get(0), context));

		NonNullList<ChanceResult> recipeOutputs = recipe.getRollableResults();

		int size = recipeOutputs.size();
		int centerX = size > 1 ? 1 : 10;
		int centerY = size > 2 ? 1 : 10;

		for (int i = 0; i < size; i++) {
			int xOffset = centerX + (i % 2 == 0 ? 0 : 19);
			int yOffset = centerY + ((i / 2) * 19);

			int index = i;
			builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_GRID_X + xOffset, OUTPUT_GRID_Y + yOffset)
					.add(recipeOutputs.get(i).stack())
					.addRichTooltipCallback((slotView, tooltip) -> {
						ChanceResult output = recipeOutputs.get(index);
						float chance = output.chance();
						if (chance != 1)
							tooltip.add(TextUtils.JEI("chance", chance < 0.01 ? "<1" : (int) (chance * 100))
									.withStyle(ChatFormatting.GOLD));
					});
		}
	}

	@Override
	public void draw(RecipeHolder<CuttingBoardRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_IMAGE, 0, 0, 0, 0, 117, 57, 256, 256);
		CuttingBoardRecipe recipe = holder.value();
		NonNullList<ChanceResult> recipeOutputs = recipe.getRollableResults();

		int size = recipe.getResults().size();
		int centerX = size > 1 ? 0 : 9;
		int centerY = size > 2 ? 0 : 9;

		for (int i = 0; i < size; i++) {
			int xOffset = centerX + (i % 2 == 0 ? 0 : 19);
			int yOffset = centerY + ((i / 2) * 19);

			if (recipeOutputs.get(i).chance() != 1) {
				guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_IMAGE, OUTPUT_GRID_X + xOffset, OUTPUT_GRID_Y + yOffset, 18, 58, 18, 18, 256, 256);
			} else {
				guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_IMAGE, OUTPUT_GRID_X + xOffset, OUTPUT_GRID_Y + yOffset, 0, 58, 18, 18, 256, 256);
			}
		}
	}
}

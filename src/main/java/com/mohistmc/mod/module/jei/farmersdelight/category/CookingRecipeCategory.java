package com.mohistmc.mod.module.jei.farmersdelight.category;

import com.google.common.base.Suppliers;
import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CookingPotRecipe;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModItems;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModRecipeTypes;
import com.mohistmc.mod.module.farmersdelight.common.utility.ClientRenderUtils;
import com.mohistmc.mod.module.farmersdelight.common.utility.RecipeUtils;
import com.mohistmc.mod.module.farmersdelight.common.utility.TextUtils;
import com.mohistmc.mod.module.jei.CreateCategory;
import com.mohistmc.mod.module.jei.JeiClientPlugin;
import com.mohistmc.mod.module.jei.renderer.IconRenderer;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;

// Mirrors the Create JEI category style: no-arg constructor, no IGuiHelper.
// The background and UI icons are drawn directly in draw() via GuiGraphics.blit.
@ParametersAreNonnullByDefault
public class CookingRecipeCategory implements IRecipeCategory<RecipeHolder<CookingPotRecipe>>
{
	private static final Identifier WIDGET_BACKGROUND =
			Identifier.fromNamespaceAndPath(FarmersDelight.MODID, "textures/gui/jei/cooking_pot.png");
	private static final Identifier INTERFACE_IMAGE =
			Identifier.fromNamespaceAndPath(FarmersDelight.MODID, "textures/gui/cooking_pot.png");
	private final Component title;

	public CookingRecipeCategory() {
		title = TextUtils.JEI("cooking");
	}

	@Override
	public IRecipeType<RecipeHolder<CookingPotRecipe>> getRecipeType() {
		return JeiClientPlugin.COOKING;
	}

	@Override
	public Component getTitle() {
		return this.title;
	}

	@Override
	public int getWidth() {
		return 116;
	}

	@Override
	public int getHeight() {
		return 56;
	}

	@Override
	public IDrawable getIcon() {
		return new IconRenderer(ModItems.COOKING_POT.get());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CookingPotRecipe> holder, IFocusGroup focusGroup) {
		CookingPotRecipe recipe = holder.value();
		NonNullList<Ingredient> recipeIngredients = recipe.getIngredients();
		ItemStack resultStack = recipe.assemble(null); // This is usually very bad, but CookingPotRecipe always
		// has the same output, so it's fine to pass null in this case.
		ItemStack containerStack = recipe.getOutputContainer();

		// Use Create's getStacks + addItemStacks instead of .add(Ingredient): the latter triggers
		// Ingredient.display(), which crashes on CompoundIngredient in MC 26.2 (stream reused).
		Supplier<ContextMap> context = Suppliers.memoize(CreateCategory::createIngredientContext);
		int borderSlotSize = 18;
		for (int row = 0; row < 2; ++row) {
			for (int column = 0; column < 3; ++column) {
				int inputIndex = row * 3 + column;
				if (inputIndex < recipeIngredients.size()) {
					builder.addSlot(RecipeIngredientRole.INPUT, (column * borderSlotSize) + 1, (row * borderSlotSize) + 1)
							.addItemStacks(CreateCategory.getStacks(recipeIngredients.get(inputIndex), context));
				}
			}
		}

		builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 10).add(resultStack);

		if (!containerStack.isEmpty()) {
			builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 63, 39).add(containerStack);
		}

		builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 39).add(resultStack);
	}

	@Override
	public void draw(RecipeHolder<CookingPotRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, WIDGET_BACKGROUND, 0, 0, 0, 0, 116, 56, 256, 256);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, INTERFACE_IMAGE, 60, 9, 176, 15, 24, 17, 256, 256);    // arrow
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, INTERFACE_IMAGE, 18, 39, 176, 0, 17, 15, 256, 256);    // heat indicator
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, INTERFACE_IMAGE, 64, 2, 176, 32, 8, 11, 256, 256);     // time icon
		if (holder.value().getExperience() > 0) {
			guiGraphics.blit(RenderPipelines.GUI_TEXTURED, INTERFACE_IMAGE, 63, 21, 176, 43, 9, 9, 256, 256); // experience icon
		}
	}

	@Override
	public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<CookingPotRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		if (ClientRenderUtils.isCursorInsideBounds(61, 2, 22, 28, mouseX, mouseY)) {
			int cookTime = recipe.value().getCookTime();
			if (cookTime > 0) {
				int cookTimeSeconds = cookTime / 20;
				tooltip.add(Component.translatable("gui.jei.category.smelting.time.seconds", cookTimeSeconds));
			}
			float experience = recipe.value().getExperience();
			if (experience > 0) {
				tooltip.add(Component.translatable("gui.jei.category.smelting.experience", experience));
			}
		}
	}
}

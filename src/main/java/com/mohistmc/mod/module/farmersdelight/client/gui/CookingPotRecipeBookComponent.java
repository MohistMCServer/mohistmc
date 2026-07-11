package com.mohistmc.mod.module.farmersdelight.client.gui;

import javax.annotation.Nonnull;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.block.entity.container.CookingPotMenu;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModRecipeCategories;
import com.mohistmc.mod.module.farmersdelight.common.utility.TextUtils;

public class CookingPotRecipeBookComponent extends RecipeBookComponent<CookingPotMenu>
{
	protected static final WidgetSprites RECIPE_BOOK_BUTTONS = new WidgetSprites(
			Identifier.fromNamespaceAndPath(FarmersDelight.MODID, "recipe_book/cooking_pot_enabled"),
			Identifier.fromNamespaceAndPath(FarmersDelight.MODID, "recipe_book/cooking_pot_disabled"),
			Identifier.fromNamespaceAndPath(FarmersDelight.MODID, "recipe_book/cooking_pot_enabled_highlighted"),
			Identifier.fromNamespaceAndPath(FarmersDelight.MODID, "recipe_book/cooking_pot_disabled_highlighted"));

	public CookingPotRecipeBookComponent(CookingPotMenu menu) {
		super(menu, ModRecipeCategories.createCookingPotTabInfo());
	}

	@Override
	protected WidgetSprites getFilterButtonTextures() {
		return RECIPE_BOOK_BUTTONS;
	}

	public void hide() {
		this.setVisible(false);
	}

	@Override
	@Nonnull
	protected Component getRecipeFilterName() {
		return TextUtils.container("recipe_book.cookable");
	}

	@Override
	protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay recipe, ContextMap context) {
		// Set the result in the meal display slot (index 6)
		if (this.menu.slots.size() > 6) {
			Slot resultSlot = this.menu.slots.get(6);
			ghostSlots.setResult(resultSlot, context, recipe.result());
		}
	}

	@Override
	protected boolean isCraftingSlot(Slot slot) {
		return slot.index < 6;
	}

	@Override
	protected void selectMatchingRecipes(RecipeCollection collection, StackedItemContents stackedContents) {
		collection.selectRecipes(stackedContents, display -> true);
	}
}

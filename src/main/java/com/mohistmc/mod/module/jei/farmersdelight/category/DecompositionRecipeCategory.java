package com.mohistmc.mod.module.jei.farmersdelight.category;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModBlocks;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModItems;
import com.mohistmc.mod.module.farmersdelight.common.tag.ModTags;
import com.mohistmc.mod.module.farmersdelight.common.utility.ClientRenderUtils;
import com.mohistmc.mod.module.farmersdelight.common.utility.TextUtils;
import com.mohistmc.mod.module.jei.JeiClientPlugin;
import com.mohistmc.mod.module.jei.farmersdelight.resource.DecompositionDummy;
import com.mohistmc.mod.module.jei.renderer.IconRenderer;
import java.util.ArrayList;
import java.util.List;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

// Mirrors the Create JEI category style: no-arg constructor, no IGuiHelper.
@ParametersAreNonnullByDefault
public class DecompositionRecipeCategory implements IRecipeCategory<DecompositionDummy>
{
	public static final Identifier UID = Identifier.fromNamespaceAndPath(FarmersDelight.MODID, "decomposition");
	private static final int slotSize = 22;
	private static final Identifier BACKGROUND_IMAGE =
			Identifier.fromNamespaceAndPath(FarmersDelight.MODID, "textures/gui/jei/decomposition.png");

	private final Component title;
	private final ItemStack organicCompost;
	private final ItemStack richSoil;

	public DecompositionRecipeCategory() {
		title = TextUtils.JEI("decomposition");
		organicCompost = new ItemStack(ModBlocks.ORGANIC_COMPOST.get());
		richSoil = new ItemStack(ModItems.RICH_SOIL.get());
	}

	@Override
	public IRecipeType<DecompositionDummy> getRecipeType() {
		return JeiClientPlugin.DECOMPOSITION;
	}

	@Override
	public Component getTitle() {
		return this.title;
	}

	@Override
	public int getWidth() {
		return 118;
	}

	@Override
	public int getHeight() {
		return 80;
	}

	@Override
	public IDrawable getIcon() {
		return new IconRenderer(richSoil);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, DecompositionDummy recipe, IFocusGroup focusGroup) {
		List<ItemStack> accelerators = new ArrayList<>();
		BuiltInRegistries.BLOCK.getTagOrEmpty(ModTags.Blocks.COMPOST_ACTIVATORS).forEach(f -> accelerators.add(new ItemStack(f.value())));

		builder.addSlot(RecipeIngredientRole.INPUT, 9, 26).add(organicCompost);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 93, 26).add(richSoil);
		builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 64, 54).addItemStacks(accelerators);
	}

	@Override
	public void draw(DecompositionDummy recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_IMAGE, 0, 0, 0, 0, 118, 80, 256, 256);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_IMAGE, 63, 53, 119, 0, slotSize, slotSize, 256, 256);
	}

	@Override
	public void getTooltip(ITooltipBuilder tooltip, DecompositionDummy recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		if (ClientRenderUtils.isCursorInsideBounds(40, 38, 11, 11, mouseX, mouseY)) {
			tooltip.add(TextUtils.JEI("decomposition.light"));
		}
		if (ClientRenderUtils.isCursorInsideBounds(53, 38, 11, 11, mouseX, mouseY)) {
			tooltip.add(TextUtils.JEI("decomposition.fluid"));
		}
		if (ClientRenderUtils.isCursorInsideBounds(67, 38, 11, 11, mouseX, mouseY)) {
			tooltip.add(TextUtils.JEI("decomposition.accelerators"));
		}
	}
}

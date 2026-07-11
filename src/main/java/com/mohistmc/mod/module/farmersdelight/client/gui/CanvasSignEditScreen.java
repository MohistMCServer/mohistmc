package com.mohistmc.mod.module.farmersdelight.client.gui;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.block.state.CanvasSign;

public class CanvasSignEditScreen extends SignEditScreen
{
	@Nullable
	protected DyeColor dye;
	private final Identifier texture;

	public CanvasSignEditScreen(SignBlockEntity signBlockEntity, boolean isFront, boolean isTextFilteringEnabled) {
		super(signBlockEntity, isFront, isTextFilteringEnabled);
		Block block = signBlockEntity.getBlockState().getBlock();
		if (block instanceof CanvasSign canvasSign) {
			this.dye = canvasSign.getBackgroundColor();
		}
		String dyeName = dye != null ? "_" + dye.getName() : "";
		this.texture = Identifier.fromNamespaceAndPath(FarmersDelight.MODID, "canvas" + dyeName + ".png").withPrefix("textures/gui/signs/");
	}

	@Override
	protected void extractSignBackground(GuiGraphicsExtractor graphics) {
		graphics.pose().translate(0.0F, 27.0F);
		graphics.pose().scale(SignEditScreen.MAGIC_BACKGROUND_SCALE, SignEditScreen.MAGIC_BACKGROUND_SCALE);
		graphics.blit(RenderPipelines.GUI_TEXTURED, this.texture, -12, -13, 0.0F, 0.0F, 24, 26, 24, 26);
	}
}

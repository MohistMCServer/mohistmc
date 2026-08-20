package com.mohistmc.mod.module.farmersdelight.common.block.state;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.item.DyeColor;

public interface CanvasSign
{
	/**
	 * Returns this sign's background dye color. If null, the sign is uncolored (beige).
	 */
	@Nullable
	DyeColor getBackgroundColor();

	/**
	 * Checks a config to determine if the background color is considered "dark".
	 */
	default boolean isDarkBackground() {
		DyeColor backgroundDye = this.getBackgroundColor();
		return backgroundDye != null && List.of("gray", "purple", "blue", "brown", "green", "red", "black").contains(backgroundDye.getName());
	}
}

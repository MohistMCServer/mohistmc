package com.mohistmc.mod.client.renderer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;


/**
 * @author Mgazul
 * @date 2026/4/16 00:26
 */
@SuppressWarnings("deprecation")
public class ItemBorderRenderer {
    public static void renderRarityBorder(GuiGraphicsExtractor guiGraphics, ItemStack itemStack, int x, int y) {
        if (itemStack.isEmpty()) {
            return;
        }

        Rarity rarity = itemStack.getRarity();
        int borderColor = TextColor.fromLegacyFormat(rarity.color()).getValue();
        final String textureName = "rarity_" + rarity.ordinal();
        try {
            final Identifier textureLocation = Identifier.parse("mohistmc:textures/border/" + textureName + ".png");
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, textureLocation, x, y, 0.0f, 0.0f, 16, 16, 16, 16);
        }
        catch (final Exception e) {
            if (rarity != Rarity.COMMON) {
                int alphaMask = 0x80000000;
                int translucentColor = (borderColor & 0x00FFFFFF) | alphaMask;
                guiGraphics.fill(x, y, x + 16, y + 16, translucentColor);
            }
        }


    }
}

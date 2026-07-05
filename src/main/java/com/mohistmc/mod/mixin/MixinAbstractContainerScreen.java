package com.mohistmc.mod.mixin;

import com.mohistmc.mod.client.renderer.ItemBorderRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/4/16 00:33
 */
@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen {

    @Inject(method = "extractSlot", at = @At("HEAD"))
    private void renderSlotWithRarityBorder(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        ItemStack itemStack = slot.getItem();
        if (!itemStack.isEmpty()) {
            ItemBorderRenderer.renderRarityBorder(graphics, itemStack, slot.x, slot.y);
        }
    }
}

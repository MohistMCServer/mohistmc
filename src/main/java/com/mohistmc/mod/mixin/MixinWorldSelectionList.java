package com.mohistmc.mod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/3/19 16:20
 */
@Mixin({ WorldSelectionList.class })
public abstract class MixinWorldSelectionList
{
    @Redirect(method = { "handleNewLevels" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;openFresh(Lnet/minecraft/client/Minecraft;Ljava/lang/Runnable;)V"))
    private void redirect$loadLevels(Minecraft minecraft, Runnable onClose) {
    }
}

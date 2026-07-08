package com.mohistmc.mod.mixin.loadingbackgrounds;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin {

    @Shadow
    private float currentProgress;

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void inject$render$tail(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {

    }
}

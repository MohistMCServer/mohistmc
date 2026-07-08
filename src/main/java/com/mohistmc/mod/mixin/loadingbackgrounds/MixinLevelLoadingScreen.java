package com.mohistmc.mod.mixin.loadingbackgrounds;

import com.mohistmc.mod.client.gui.LoadingBackgrounds;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelLoadingScreen.class})
public class MixinLevelLoadingScreen {
    @Shadow
    @Final
    private LevelLoadTracker loadTracker;

    @Inject(method = {"extractRenderState"}, at = {@At("HEAD")}, cancellable = true)
    private void inject$render$head(final GuiGraphicsExtractor pGuiGraphics, final int pMouseX, final int pMouseY, final float pPartialTick, final CallbackInfo ci) {
        pGuiGraphics.fill(0, 0, pGuiGraphics.guiWidth(), pGuiGraphics.guiHeight(), 0xFFFFFFFF);
        new LoadingBackgrounds().renderProgress(Mth.clamp(this.loadTracker.serverProgress(), 0.0f, 1.0f), pGuiGraphics);
        ci.cancel();
    }
}
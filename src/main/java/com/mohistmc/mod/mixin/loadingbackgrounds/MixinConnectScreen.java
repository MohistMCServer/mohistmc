package com.mohistmc.mod.mixin.loadingbackgrounds;

import com.mohistmc.mod.client.LoadingBackgrounds;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public abstract class MixinConnectScreen extends Screen {
    private MixinConnectScreen(final Component title) {
        super(title);
    }

    @Inject(method = {"extractRenderState"}, at = {@At("HEAD")}, cancellable = true)
    private void inject$render$head(final GuiGraphicsExtractor pGuiGraphics, final int pMouseX, final int pMouseY, final float pPartialTick, final CallbackInfo ci) {
        pGuiGraphics.fill(0, 0, pGuiGraphics.guiWidth(), pGuiGraphics.guiHeight(), 0xFFFFFFFF);
        new LoadingBackgrounds().renderProgress(Mth.clamp(100 / 100.0f, 0.0f, 1.0f), pGuiGraphics);
        ci.cancel();
    }
}

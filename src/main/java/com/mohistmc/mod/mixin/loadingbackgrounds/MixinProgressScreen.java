package com.mohistmc.mod.mixin.loadingbackgrounds;

import com.mohistmc.mod.client.LoadingBackgrounds;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ProgressScreen.class})
public abstract class MixinProgressScreen extends Screen {

    @Shadow
    private int progress;

    private MixinProgressScreen(final Component title) {
        super(title);
    }

    @Inject(method = {"extractRenderState"}, at = {@At("HEAD")}, cancellable = true)
    private void inject$render$head(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a, final CallbackInfo ci) {
        graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), 0xFFFFFFFF);
        new LoadingBackgrounds().renderProgress(Mth.clamp(progress / 100.0f, 0.0f, 1.0f), graphics);
        ci.cancel();
    }
}


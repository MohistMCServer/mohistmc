package com.mohistmc.mod.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.tutorial.Tutorial;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ Tutorial.class })
public abstract class TutorialMixin
{
    @Inject(method = { "tick" }, at = { @At("HEAD") }, cancellable = true)
    private void tutorial_tick(final CallbackInfo ci) {
        ci.cancel();
    }
}

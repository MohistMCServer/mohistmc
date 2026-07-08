package com.mohistmc.mod.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = { PrimaryLevelData.class }, remap = false)
public class PrimaryLevelDataMixinNeo
{
    @Inject(method = { "hasConfirmedExperimentalWarning" }, at = { @At("HEAD") }, cancellable = true)
    public void hasConfirmedExperimentalWarning(final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}

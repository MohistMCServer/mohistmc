package com.mohistmc.mod.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = { LevelSummary.class }, priority = 1001)
public class LevelSummaryMixin
{
    @Inject(method = { "isExperimental" }, at = { @At("RETURN") }, cancellable = true)
    public void isExperimental(final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}

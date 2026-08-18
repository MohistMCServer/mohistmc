package com.mohistmc.mod.mixin.create;

import com.mohistmc.mod.module.create.Create;
import com.mohistmc.mod.module.create.content.kinetics.fan.processing.FanProcessingTypeRegistry;
import com.mohistmc.mod.module.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin {
    @Inject(method = "freeze()V", at = @At("HEAD"))
    private static void onInitialize(CallbackInfo ci) {
        Create.register();
    }

    @Inject(method = "freeze()V", at = @At("TAIL"))
    private static void afterFreeze(CallbackInfo ci) {
        ArmInteractionPointType.register();
        FanProcessingTypeRegistry.register();
    }
}

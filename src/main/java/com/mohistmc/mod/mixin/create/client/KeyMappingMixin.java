package com.mohistmc.mod.mixin.create.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mohistmc.mod.module.ponder.enums.PonderKeybinds;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {
    @WrapOperation(method = "resetMapping()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;registerMapping(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V"))
    private static void skip(KeyMapping instance, InputConstants.Key key, Operation<Void> original) {
        if (instance == PonderKeybinds.PONDER) {
            return;
        }
        original.call(instance, key);
    }
}

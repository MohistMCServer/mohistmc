package com.mohistmc.mod.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ CreateWorldScreen.class })
public class CreateWorldScreenMixin
{
    @ModifyVariable(method = "tryApplyNewDataPacks", at = @At("HEAD"), argsOnly = true, name = "isDataPackScreen")
    public boolean dontShowWarning(boolean isDataPackScreen) {
        return false;
    }
}
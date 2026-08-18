package com.mohistmc.mod.mixin;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({ CreateWorldScreen.class })
public class CreateWorldScreenMixin
{
    @ModifyVariable(method = "tryApplyNewDataPacks", at = @At("HEAD"), argsOnly = true, name = "isDataPackScreen")
    public boolean dontShowWarning(boolean isDataPackScreen) {
        return false;
    }
}
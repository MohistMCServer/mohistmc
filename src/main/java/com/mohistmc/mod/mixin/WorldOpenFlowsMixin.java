package com.mohistmc.mod.mixin;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Debug(export = true)
@Mixin({ WorldOpenFlows.class })
public class WorldOpenFlowsMixin
{
    @ModifyVariable(method = {"confirmWorldCreation"}, at = @At("HEAD"), argsOnly = true, name = "lifecycle")
    private static Lifecycle alwaysStable(Lifecycle lifecycle) {
        return Lifecycle.stable();
    }

    @ModifyVariable(method = {"openWorldCheckWorldStemCompatibility"}, at = @At("STORE"), name = "unstable")
    public boolean no(boolean unstable) {
        return false;
    }
}

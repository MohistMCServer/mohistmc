package com.mohistmc.mod.mixin.create.client;

import com.mohistmc.mod.module.create.AllSynchedDatas;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Inject(method = "isUnderWater()Z", at = @At("HEAD"), cancellable = true)
    private void isSubmergedInWater(CallbackInfoReturnable<Boolean> cir) {
        if (AllSynchedDatas.HEAVY_BOOTS.get((LocalPlayer) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}

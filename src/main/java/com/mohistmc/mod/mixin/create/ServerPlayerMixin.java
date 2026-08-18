package com.mohistmc.mod.mixin.create;

import com.mohistmc.mod.module.create.content.equipment.bell.HauntedBellPulser;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "tick()V", at = @At("TAIL"))
    private void tickPost(CallbackInfo ci) {
        HauntedBellPulser.hauntedBellCreatesPulse((ServerPlayer) (Object) this);
    }
}

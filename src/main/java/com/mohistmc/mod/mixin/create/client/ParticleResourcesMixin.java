package com.mohistmc.mod.mixin.create.client;

import com.mohistmc.mod.module.create.client.AllParticleTypes;
import net.minecraft.client.particle.ParticleResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleResources.class)
public class ParticleResourcesMixin {
    @Inject(method = "registerProviders()V", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        AllParticleTypes.register((ParticleResources) (Object) this);
    }
}

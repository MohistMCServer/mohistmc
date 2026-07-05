package com.mohistmc.mod.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpellParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/4/16 05:05
 */
@Mixin(ParticleEngine.class)
public class MixinParticleEngine {

    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    private void onAddParticle(Particle p, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        Camera camera = minecraft.gameRenderer.mainCamera();

        if (p instanceof SpellParticle) {
            if (minecraft.options.getCameraType().isFirstPerson()) {
                double distanceSquared = camera.position().distanceToSqr(p.getPos().x, p.getPos().y, p.getPos().z);

                // Define a threshold distance (e.g., 2 blocks = 4 units squared)
                double thresholdDistanceSquared = 4.0;
                if (distanceSquared < thresholdDistanceSquared) {
                    ci.cancel();
                }
            }
        }
    }
}

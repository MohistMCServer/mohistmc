package com.mohistmc.mod.mixin.create.client;

import com.mohistmc.mod.module.create.content.contraptions.minecart.capability.CapabilityMinecartController;
import com.mohistmc.mod.module.flywheel.impl.visualization.VisualizationEventHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.multiplayer.ClientLevel$EntityCallbacks")
public class EntityCallbacksMixin {
    @Shadow
    @Final
    ClientLevel this$0;

    @Inject(method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    private void onEntityLeaveLevel(Entity entity, CallbackInfo ci) {
        VisualizationEventHandler.onEntityLeaveLevel(this$0, entity);
        CapabilityMinecartController.onEntityDeath(this$0, entity);
    }
}

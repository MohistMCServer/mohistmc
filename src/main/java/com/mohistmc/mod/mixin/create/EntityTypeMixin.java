package com.mohistmc.mod.mixin.create;

import com.mohistmc.mod.module.create.AllEntityTypes;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public class EntityTypeMixin {
    @Inject(method = "trackDeltas()Z", at = @At("HEAD"), cancellable = true)
    private void alwaysUpdateVelocity(CallbackInfoReturnable<Boolean> cir) {
        if (AllEntityTypes.NOT_SEND_VELOCITY.contains((EntityType<?>) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}

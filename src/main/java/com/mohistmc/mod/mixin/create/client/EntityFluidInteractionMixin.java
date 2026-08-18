package com.mohistmc.mod.mixin.create.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.mod.module.create.infrastructure.fluids.FlowableFluid;
import com.mohistmc.mod.module.create.infrastructure.fluids.FluidInteractionPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityFluidInteraction;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityFluidInteraction.class)
public class EntityFluidInteractionMixin implements FluidInteractionPredicate {
    @Unique
    private boolean inModFluid;

    @Inject(method = "update(Lnet/minecraft/world/entity/Entity;Z)V", at = @At("HEAD"))
    private void clear(Entity entity, boolean ignoreCurrent, CallbackInfo ci) {
        inModFluid = false;
    }

    @Inject(method = "update(Lnet/minecraft/world/entity/Entity;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;getY()I"))
    private void update(Entity entity, boolean ignoreCurrent, CallbackInfo ci, @Local FluidState fluidState) {
        if (!inModFluid) {
            inModFluid = fluidState.getType() instanceof FlowableFluid;
        }
    }

    @Override
    public boolean create$inModFluid() {
        return inModFluid;
    }
}

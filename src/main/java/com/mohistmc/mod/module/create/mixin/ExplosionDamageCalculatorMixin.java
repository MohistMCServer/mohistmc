package com.mohistmc.mod.module.create.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.foundation.block.ResistanceControlBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ExplosionDamageCalculator.class)
public class ExplosionDamageCalculatorMixin {
    @WrapOperation(method = "getBlockExplosionResistance(Lnet/minecraft/world/level/Explosion;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getExplosionResistance(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;)F"))
    private float getBlastResistance(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        Explosion explosion,
        Operation<Float> original
    ) {
        if (state.getBlock() instanceof ResistanceControlBlock controlBlock) {
            return controlBlock.getResistance(level, pos);
        }
        return original.call(state, level, pos, explosion);
    }
}

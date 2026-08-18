package com.mohistmc.mod.mixin.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mohistmc.mod.module.create.foundation.block.WeakPowerControlBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SignalGetter.class)
public interface SignalGetterMixin {
    @WrapOperation(method = "getSignal(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;shouldCheckWeakPower(Lnet/minecraft/world/level/SignalGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"))
    private boolean skip(
        BlockState state,
        SignalGetter signalGetter,
        BlockPos pos,
        Direction direction,
        Operation<Boolean> original
    ) {
        if (state.getBlock() instanceof WeakPowerControlBlock block) {
            return block.shouldCheckWeakPower(state, (SignalGetter) this, pos, direction);
        }
        return original.call(state, signalGetter, pos, direction);
    }
}

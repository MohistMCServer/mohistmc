package com.mohistmc.mod.module.create.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.foundation.block.SlipperinessControlBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Leashable.class)
public interface LeashableMixin {
    @WrapOperation(method = "angularFriction(Lnet/minecraft/world/entity/Entity;)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F"))
    private static float getSlipperiness(
        BlockState state,
        LevelReader level,
        BlockPos pos,
        Entity entity,
        Operation<Float> original
    ) {
        if (state.getBlock() instanceof SlipperinessControlBlock controlBlock) {
            return controlBlock.getSlipperiness(level, pos);
        }
        return original.call(state, level, pos, entity);
    }
}

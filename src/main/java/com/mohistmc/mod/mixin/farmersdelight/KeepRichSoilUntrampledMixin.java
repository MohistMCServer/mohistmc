package com.mohistmc.mod.mixin.farmersdelight;

import com.mohistmc.mod.module.farmersdelight.common.block.RichSoilFarmlandBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
public class KeepRichSoilUntrampledMixin {
    @Inject(at = @At(value = "HEAD"), method = "turnToDirt", cancellable = true)
    private static void turnToDirt(Entity entity, BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
        if (state.getBlock() instanceof RichSoilFarmlandBlock) ci.cancel();
    }
}

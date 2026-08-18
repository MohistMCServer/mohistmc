package com.mohistmc.mod.mixin.create.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.mod.module.create.client.AllBlockTints;
import net.minecraft.client.color.block.BlockColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockColors.class)
public class BlockColorsMixin {
    @Inject(method = "createDefault()Lnet/minecraft/client/color/block/BlockColors;", at = @At("TAIL"))
    private static void addColors(CallbackInfoReturnable<BlockColors> cir, @Local BlockColors colors) {
        AllBlockTints.register(colors);
    }
}

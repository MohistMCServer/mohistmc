package com.mohistmc.mod.mixin.create.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.mod.module.create.client.vanillin.VanillaVisuals;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {
    @Inject(method = "onResourceManagerReload(Lnet/minecraft/server/packs/resources/ResourceManager;)V", at = @At("TAIL"))
    private void onReload(
        ResourceManager resourceManager,
        CallbackInfo ci,
        @Local BlockEntityRendererProvider.Context context
    ) {
        VanillaVisuals.onReloadModel(context.entityModelSet(), context.blockModelResolver());
    }
}

package com.mohistmc.mod.mixin.create.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.mod.module.create.client.flywheel.lib.model.baked.PartialModelEventHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.util.thread.ParallelMapTransform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelBakery.class)
public class ModelBakeryMixin {
    @ModifyReturnValue(method = "bakeModels(Lnet/minecraft/client/resources/model/sprite/MaterialBaker;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", at = @At("RETURN"))
    private CompletableFuture<ModelBakery.BakingResult> bakeModels(
        CompletableFuture<ModelBakery.BakingResult> bakedModelFuture,
        @Local(argsOnly = true) Executor taskExecutor,
        @Local ModelBakery.ModelBakerImpl baker
    ) {
        return ParallelMapTransform.schedule(
            PartialModelEventHandler.getRegisterAdditional(), (id, model) -> {
                BlockStateModel blockStateModel = new SingleVariant(SimpleModelWrapper.bake(
                    baker,
                    id,
                    BlockModelRotation.IDENTITY
                ));
                PartialModelEventHandler.onBakingCompleted(model, blockStateModel);
                return blockStateModel;
            }, taskExecutor
        ).thenAccept(PartialModelEventHandler::onBakingCompleted).thenCompose(v -> bakedModelFuture);
    }
}

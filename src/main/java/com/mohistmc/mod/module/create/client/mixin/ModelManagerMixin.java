package com.mohistmc.mod.module.create.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModelEventHandler;
import java.util.Map;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ClientItemInfoLoader;
import net.minecraft.client.resources.model.ModelDiscovery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelManager.class)
public class ModelManagerMixin {
    @Inject(method = "discoverModelDependencies(Ljava/util/Map;Lnet/minecraft/client/resources/model/BlockStateModelLoader$LoadedModels;Lnet/minecraft/client/resources/model/ClientItemInfoLoader$LoadedClientInfos;Lnet/neoforged/neoforge/client/model/standalone/StandaloneModelLoader$LoadedModels;)Lnet/minecraft/client/resources/model/ModelManager$ResolvedModels;", at = @At(value = "NEW", target = "(Lnet/minecraft/client/resources/model/ResolvedModel;Ljava/util/Map;)Lnet/minecraft/client/resources/model/ModelManager$ResolvedModels;"))
    private static void collect(
        Map<Identifier, UnbakedModel> allModels,
        BlockStateModelLoader.LoadedModels blockStateModels,
        ClientItemInfoLoader.LoadedClientInfos itemInfos,
        StandaloneModelLoader.LoadedModels standaloneModels,
        CallbackInfoReturnable<ModelManager.ResolvedModels> cir,
        @Local ModelDiscovery result
    ) {
        PartialModelEventHandler.getRegisterAdditional().keySet().forEach(result::getOrCreateModel);
    }
}

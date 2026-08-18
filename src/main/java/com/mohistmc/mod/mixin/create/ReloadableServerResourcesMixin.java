package com.mohistmc.mod.mixin.create;

import com.mohistmc.mod.module.create.content.kinetics.mixer.PotionRecipe;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import net.minecraft.commands.Commands;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
    @Inject(method = "lambda$loadResources$2(Lnet/minecraft/server/ReloadableServerRegistries$LoadResult;Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/commands/Commands$CommandSelection;Ljava/util/List;Lnet/minecraft/server/permissions/PermissionSet;Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;)Ljava/util/concurrent/CompletionStage;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/SimpleReloadInstance;create(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Z)Lnet/minecraft/server/packs/resources/ReloadInstance;"))
    private static void onReload(
        ReloadableServerRegistries.LoadResult fullRegistries,
        FeatureFlagSet enabledFeatures,
        Commands.CommandSelection commandSelection,
        List<?> updatedContextTags,
        PermissionSet functionCompilationPermissions,
        ResourceManager resourceManager,
        Executor backgroundExecutor,
        Executor mainThreadExecutor,
        List<?> pendingComponents,
        CallbackInfoReturnable<CompletionStage<?>> cir
    ) {
        PotionRecipe.data = new PotionRecipe.ReloadData(fullRegistries.lookupWithUpdatedTags(), enabledFeatures);
    }
}

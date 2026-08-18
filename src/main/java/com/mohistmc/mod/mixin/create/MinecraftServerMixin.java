package com.mohistmc.mod.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.mod.module.create.Create;
import com.mohistmc.mod.module.create.content.contraptions.ContraptionHandler;
import com.mohistmc.mod.module.create.content.contraptions.actors.trainControls.ControlsServerHandler;
import com.mohistmc.mod.module.create.content.contraptions.minecart.CouplingPhysics;
import com.mohistmc.mod.module.create.content.contraptions.minecart.capability.CapabilityMinecartController;
import com.mohistmc.mod.module.create.content.kinetics.chainConveyor.ServerChainConveyorHandler;
import com.mohistmc.mod.module.create.content.redstone.link.controller.LinkedControllerServerHandler;
import com.mohistmc.mod.module.create.foundation.utility.ServerSpeedProvider;
import com.mohistmc.mod.module.create.foundation.utility.TickBasedCache;
import java.util.function.BooleanSupplier;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow
    public abstract RegistryAccess.Frozen registryAccess();

    @Inject(method = "tickServer(Ljava/util/function/BooleanSupplier;)V", at = @At("TAIL"))
    void tick(BooleanSupplier haveTime, CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer) (Object) this;
        Create.SCHEMATIC_RECEIVER.tick();
        ServerSpeedProvider.serverTick(server);
        Create.RAILWAYS.sync.serverTick(server);
        ServerChainConveyorHandler.tick(server);
        TickBasedCache.tick();
    }

    @Inject(method = "runServer()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;stopServer()V"))
    private void serverStopping(CallbackInfo ci) {
        Create.SCHEMATIC_RECEIVER.shutdown();
    }

    @Inject(method = "tickChildren(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tick(Ljava/util/function/BooleanSupplier;)V", shift = At.Shift.AFTER))
    private void onServerWorldTick(BooleanSupplier haveTime, CallbackInfo ci, @Local ServerLevel level) {
        ContraptionHandler.tick(level);
        CapabilityMinecartController.tick(level);
        CouplingPhysics.tick(level);
        LinkedControllerServerHandler.tick(level);
        ControlsServerHandler.tick(level);
        Create.RAILWAYS.tick(level);
        Create.LOGISTICS.tick(level);
    }
}

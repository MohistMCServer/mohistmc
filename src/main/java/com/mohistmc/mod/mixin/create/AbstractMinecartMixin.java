package com.mohistmc.mod.mixin.create;

import com.mohistmc.mod.module.create.AllSynchedDatas;
import com.mohistmc.mod.module.create.content.contraptions.minecart.capability.MinecartController;
import java.util.Optional;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecart.class)
public class AbstractMinecartMixin {
    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V", at = @At("TAIL"))
    private void readAdditionalSaveData(ValueInput input, CallbackInfo ci) {
        input.read("mohistmc:minecart_controller", MinecartController.CODEC).ifPresent(controller -> {
            AbstractMinecart minecart = (AbstractMinecart) (Object) this;
            controller.setCart(minecart);
            AllSynchedDatas.MINECART_CONTROLLER.set(minecart, Optional.of(controller));
        });
    }

    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V", at = @At("TAIL"))
    private void addAdditionalSaveData(ValueOutput output, CallbackInfo ci) {
        AllSynchedDatas.MINECART_CONTROLLER.get((AbstractMinecart) (Object) this).ifPresent(controller -> {
            output.store("mohistmc:minecart_controller", MinecartController.CODEC, controller);
        });
    }
}

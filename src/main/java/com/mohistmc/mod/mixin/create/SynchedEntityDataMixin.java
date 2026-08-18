package com.mohistmc.mod.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.mod.module.create.AllSynchedDatas;
import java.util.List;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SynchedEntityData.class)
public class SynchedEntityDataMixin {
    @Final
    @Shadow
    private SyncedDataHolder entity;

    @Inject(method = "assignValues(Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SyncedDataHolder;onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V"))
    private void onTrackedDataSet(
        List<SynchedEntityData.DataValue<?>> items,
        CallbackInfo ci,
        @Local SynchedEntityData.DataValue<?> item
    ) {
        AllSynchedDatas.onData(entity, item);
    }
}

package com.mohistmc.mod.mixin.create.client;

import com.mohistmc.mod.module.create.client.content.equipment.hats.HatState;
import com.mohistmc.mod.module.create.client.content.trains.schedule.hat.TrainHatInfo;
import com.mohistmc.mod.module.create.client.content.trains.schedule.hat.TrainHatInfoReloadListener;
import com.mohistmc.mod.module.create.client.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements HatState {
    @Unique
    @Nullable
    private PartialModel hat;
    @Unique
    private TrainHatInfo hatInfo;

    @Override
    public void create$setHat(@NonNull PartialModel hat) {
        this.hat = hat;
    }

    @Override
    @Nullable
    public PartialModel create$getHat() {
        return hat;
    }

    @Override
    public void create$updateHatInfo(@NonNull Entity entity) {
        hatInfo = TrainHatInfoReloadListener.getHatInfoFor(entity);
    }

    @Override
    public @NonNull TrainHatInfo create$getHatInfo() {
        return hatInfo;
    }
}

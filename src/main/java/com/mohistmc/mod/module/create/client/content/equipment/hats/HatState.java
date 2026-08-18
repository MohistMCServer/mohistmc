package com.mohistmc.mod.module.create.client.content.equipment.hats;

import com.mohistmc.mod.module.create.client.content.trains.schedule.hat.TrainHatInfo;
import com.mohistmc.mod.module.create.client.flywheel.lib.model.baked.PartialModel;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public interface HatState {
    void create$setHat(PartialModel hat);

    @Nullable PartialModel create$getHat();

    void create$updateHatInfo(Entity entity);

    TrainHatInfo create$getHatInfo();
}

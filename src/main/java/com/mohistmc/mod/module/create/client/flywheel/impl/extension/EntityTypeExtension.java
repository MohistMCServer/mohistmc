package com.mohistmc.mod.module.create.client.flywheel.impl.extension;

import com.mohistmc.mod.module.create.client.flywheel.api.visualization.EntityVisualizer;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public interface EntityTypeExtension<T extends Entity> {
    @Nullable EntityVisualizer<? super T> flywheel$getVisualizer();

    void flywheel$setVisualizer(@Nullable EntityVisualizer<? super T> visualizer);
}

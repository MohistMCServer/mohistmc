package com.mohistmc.mod.module.create.client.flywheel.lib.visual.component;

import com.mohistmc.mod.module.create.client.flywheel.api.visual.DynamicVisual;

public interface EntityComponent {
    void beginFrame(DynamicVisual.Context context);

    void delete();
}

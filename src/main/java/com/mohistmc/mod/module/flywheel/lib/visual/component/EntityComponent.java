package com.mohistmc.mod.module.flywheel.lib.visual.component;

import com.mohistmc.mod.module.flywheel.api.visual.DynamicVisual;

public interface EntityComponent {
    void beginFrame(DynamicVisual.Context context);

    void delete();
}

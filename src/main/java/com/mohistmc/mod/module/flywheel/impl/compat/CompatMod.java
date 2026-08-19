package com.mohistmc.mod.module.flywheel.impl.compat;

import com.mohistmc.mod.module.flywheel.impl.FlwImplXplat;

public enum CompatMod {
    IRIS("iris"), SODIUM("sodium"), SCALABLELUX("scalablelux");

    public final String id;
    public final boolean isLoaded;

    CompatMod(String modId) {
        id = modId;
        isLoaded = FlwImplXplat.INSTANCE.isModLoaded(modId);
    }
}
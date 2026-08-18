package com.mohistmc.mod.module.create.client.flywheel.impl.config;

import com.mohistmc.mod.module.create.catnip.config.ConfigBase;
import com.mohistmc.mod.module.create.client.flywheel.backend.BackendConfig;
import com.mohistmc.mod.module.create.client.flywheel.backend.compile.LightSmoothness;

public class CBackendConfig extends ConfigBase implements BackendConfig {
    public final ConfigEnum<LightSmoothness> lightSmoothness = e(
        LightSmoothness.SMOOTH,
        "lightSmoothness",
        Comments.lightSmoothness
    );

    @Override
    public String getName() {
        return "flwBackends";
    }

    @Override
    public LightSmoothness lightSmoothness() {
        return lightSmoothness.get();
    }

    private static class Comments {
        public static final String lightSmoothness = "How smooth Flywheel's shader-based lighting should be. May have a large performance impact.";
    }
}

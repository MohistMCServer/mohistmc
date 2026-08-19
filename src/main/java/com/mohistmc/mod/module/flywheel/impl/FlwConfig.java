package com.mohistmc.mod.module.flywheel.impl;

import com.mohistmc.mod.module.flywheel.api.backend.Backend;
import com.mohistmc.mod.module.flywheel.backend.BackendConfig;

public interface FlwConfig {
    String DEFAULT_BACKEND_STR = "DEFAULT";

    FlwConfig INSTANCE = FlwImplXplat.INSTANCE.getConfig();

    Backend backend();

    boolean limitUpdates();

    int workerThreads();

    BackendConfig backendConfig();
}

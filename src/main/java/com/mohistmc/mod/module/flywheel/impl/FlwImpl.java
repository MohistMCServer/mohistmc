package com.mohistmc.mod.module.flywheel.impl;

import com.mohistmc.mod.module.flywheel.backend.FlwBackend;
import com.mohistmc.mod.module.flywheel.impl.registry.IdRegistryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mohistmc.mod.module.flywheel.impl.Flywheel.MOD_ID;

public final class FlwImpl {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Logger CONFIG_LOGGER = LoggerFactory.getLogger(MOD_ID + "/config");

    private FlwImpl() {
    }

    public static void init() {
        // impl
        BackendManagerImpl.init();

        // backend
        FabricFlwConfig.INSTANCE.register();
        FlwBackend.init(FlwConfig.INSTANCE.backendConfig());
    }

    public static void freezeRegistries() {
        IdRegistryImpl.freezeAll();
    }
}

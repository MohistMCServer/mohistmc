package com.mohistmc.mod.module.create.api.registry;

import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Optional registry extension point. NeoForge addons may provide this
 * interface through Java's standard service-provider mechanism.
 */
public interface CreateRegisterPlugin {
    List<CreateRegisterPlugin> PLUGINS = ServiceLoader.load(CreateRegisterPlugin.class)
        .stream()
        .sorted(Comparator.comparing(provider -> provider.type().getName()))
        .map(ServiceLoader.Provider::get)
        .toList();

    default void onBlockRegister() {
    }

    default void onFluidRegister() {
    }

    default void onDataLoaderRegister() {
    }

    default void onEntityAttributeRegister() {
    }

    static void registerBlock() {
        PLUGINS.forEach(CreateRegisterPlugin::onBlockRegister);
    }

    static void registerFluid() {
        PLUGINS.forEach(CreateRegisterPlugin::onFluidRegister);
    }

    static void registerDataLoader() {
        PLUGINS.forEach(CreateRegisterPlugin::onDataLoaderRegister);
    }

    static void registerEntityAttributes() {
        PLUGINS.forEach(CreateRegisterPlugin::onEntityAttributeRegister);
    }
}

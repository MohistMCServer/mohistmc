package com.mohistmc.mod.module.create.client.vanillin;

public interface VanillinXplat {
    VanillinXplat INSTANCE = new VanillinXplatImpl();

    boolean isDevelopmentEnvironment();

    boolean isModLoaded(String modId);
}

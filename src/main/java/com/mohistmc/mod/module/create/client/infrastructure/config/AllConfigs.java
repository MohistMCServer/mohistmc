package com.mohistmc.mod.module.create.client.infrastructure.config;

import com.mohistmc.mod.module.create.catnip.config.Builder;

import static com.mohistmc.mod.module.create.Create.MOD_ID;

public class AllConfigs {
    private static CClient client;

    public static CClient client() {
        return client;
    }

    public static void register() {
        client = Builder.create(CClient::new, MOD_ID, "client");
    }
}

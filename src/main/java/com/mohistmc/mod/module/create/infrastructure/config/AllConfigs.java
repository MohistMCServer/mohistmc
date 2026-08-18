package com.mohistmc.mod.module.create.infrastructure.config;

import com.mohistmc.mod.module.create.api.stress.BlockStressValues;
import com.mohistmc.mod.module.create.catnip.config.Builder;
import com.mohistmc.mod.module.create.foundation.utility.CreateResourceReloader;
import com.mohistmc.mod.module.create.infrastructure.packet.s2c.ServerConfigPacket;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import static com.mohistmc.mod.module.create.Create.MOD_ID;

public class AllConfigs {
    private static CCommon common;
    private static CServer server;

    public static final ResourceManagerReloadListener LISTENER = new ReloadListener();

    public static CCommon common() {
        return common;
    }

    public static CServer server() {
        return server;
    }

    public static void register() {
        common = Builder.create(CCommon::new, MOD_ID, "common");
        server = Builder.create(CServer::new, MOD_ID, "server");

        CStress stress = server().kinetics.stressValues;
        BlockStressValues.IMPACTS.registerProvider(stress::getImpact);
        BlockStressValues.CAPACITIES.registerProvider(stress::getCapacity);
    }

    private static class ReloadListener extends CreateResourceReloader {
        public ReloadListener() {
            super("config");
        }

        @Override
        public void onResourceManagerReload(ResourceManager manager) {
            ServerConfigPacket.CACHE = null;
            server.reload(null);
        }
    }
}

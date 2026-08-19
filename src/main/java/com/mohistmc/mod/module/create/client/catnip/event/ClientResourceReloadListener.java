package com.mohistmc.mod.module.create.client.catnip.event;

import com.mohistmc.mod.module.create.client.catnip.lang.LangNumberFormat;
import com.mohistmc.mod.module.ponder.Ponder;
import com.mohistmc.mod.module.create.foundation.utility.CreateResourceReloader;
import net.minecraft.server.packs.resources.ResourceManager;

public class ClientResourceReloadListener extends CreateResourceReloader {
    public ClientResourceReloadListener() {
        super("ponder");
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        LangNumberFormat.numberFormat.update();
        Ponder.invalidateRenderers();
    }
}

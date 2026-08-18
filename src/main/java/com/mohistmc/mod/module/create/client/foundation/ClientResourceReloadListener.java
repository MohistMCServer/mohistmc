package com.mohistmc.mod.module.create.client.foundation;

import com.mohistmc.mod.module.create.client.Create;
import com.mohistmc.mod.module.create.client.foundation.sound.SoundScapes;
import com.mohistmc.mod.module.create.client.infrastructure.model.TableClothModel;
import com.mohistmc.mod.module.create.content.kinetics.belt.BeltHelper;
import com.mohistmc.mod.module.create.foundation.utility.CreateResourceReloader;
import net.minecraft.server.packs.resources.ResourceManager;

public class ClientResourceReloadListener extends CreateResourceReloader {
    public ClientResourceReloadListener() {
        super("resource");
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Create.invalidateRenderers();
        SoundScapes.invalidateAll();
        BeltHelper.uprightCache.clear();
        TableClothModel.reload();
    }

}
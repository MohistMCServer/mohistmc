package com.mohistmc.mod.module.create.client.flywheel.impl;

import com.mohistmc.mod.module.create.client.flywheel.backend.engine.uniform.Uniforms;
import com.mohistmc.mod.module.create.client.flywheel.lib.model.baked.ModelRenderHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.fml.ModList;

public class FlwImplXplatImpl implements FlwImplXplat {
    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public void dispatchReloadLevelRendererEvent(ClientLevel level) {
        BackendManagerImpl.onReloadLevelRenderer(level);
        Uniforms.onReloadLevelRenderer();
        ModelRenderHelper.onReloadLevelRenderer();
        //TODO Fabric
    }

    @Override
    public String getVersionStr() {
        return Flywheel.version();
    }

    @Override
    public FlwConfig getConfig() {
        return FabricFlwConfig.INSTANCE;
    }
}

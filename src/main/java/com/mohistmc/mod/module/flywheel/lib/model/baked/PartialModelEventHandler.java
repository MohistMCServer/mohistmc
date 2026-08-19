package com.mohistmc.mod.module.flywheel.lib.model.baked;

import java.util.Map;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.resources.Identifier;

public final class PartialModelEventHandler {
    private PartialModelEventHandler() {
    }

    public static Map<Identifier, PartialModel> getRegisterAdditional() {
        return PartialModel.ALL;
    }

    public static void onBakingCompleted(PartialModel partial, BlockStateModel bakedModel) {
        partial.blockStateModel = bakedModel;
    }

    public static void onBakingCompleted(Map<Identifier, BlockStateModel> models) {
        PartialModel.populateOnInit = true;
    }
}

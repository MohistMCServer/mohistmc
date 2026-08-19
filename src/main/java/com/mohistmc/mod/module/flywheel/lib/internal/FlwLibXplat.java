package com.mohistmc.mod.module.flywheel.lib.internal;

import com.mohistmc.mod.module.flywheel.impl.FlwLibXplatImpl;
import com.mohistmc.mod.module.flywheel.lib.model.SimpleModel;
import com.mohistmc.mod.module.flywheel.lib.model.baked.BakedModelBuilder;
import com.mohistmc.mod.module.flywheel.lib.model.baked.BlockModelBuilder;

public interface FlwLibXplat {
    FlwLibXplat INSTANCE = new FlwLibXplatImpl();

    SimpleModel buildBakedModelBuilder(BakedModelBuilder builder);

    SimpleModel buildBlockModelBuilder(BlockModelBuilder builder);
}

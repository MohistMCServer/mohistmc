package com.mohistmc.mod.module.create.client.flywheel.lib.internal;

import com.mohistmc.mod.module.create.client.flywheel.impl.FlwLibXplatImpl;
import com.mohistmc.mod.module.create.client.flywheel.lib.model.SimpleModel;
import com.mohistmc.mod.module.create.client.flywheel.lib.model.baked.BakedModelBuilder;
import com.mohistmc.mod.module.create.client.flywheel.lib.model.baked.BlockModelBuilder;

public interface FlwLibXplat {
    FlwLibXplat INSTANCE = new FlwLibXplatImpl();

    SimpleModel buildBakedModelBuilder(BakedModelBuilder builder);

    SimpleModel buildBlockModelBuilder(BlockModelBuilder builder);
}

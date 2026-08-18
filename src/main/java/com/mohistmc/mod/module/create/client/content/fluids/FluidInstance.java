package com.mohistmc.mod.module.create.client.content.fluids;

import com.mohistmc.mod.module.create.client.flywheel.api.instance.InstanceHandle;
import com.mohistmc.mod.module.create.client.flywheel.api.instance.InstanceType;
import com.mohistmc.mod.module.create.client.flywheel.lib.instance.TransformedInstance;

public class FluidInstance extends TransformedInstance {

    public float progress;
    public float vScale;
    public float v0;

    public FluidInstance(InstanceType<? extends FluidInstance> type, InstanceHandle handle) {
        super(type, handle);
    }
}

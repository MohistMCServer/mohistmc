package com.mohistmc.mod.module.flywheel.lib.instance;

import com.mohistmc.mod.module.flywheel.api.instance.InstanceHandle;
import com.mohistmc.mod.module.flywheel.api.instance.InstanceType;

public class ShadowInstance extends AbstractInstance {
    public float x, y, z;
    public float entityX, entityZ;
    public float sizeX, sizeZ;
    public float alpha;
    public float radius;

    public ShadowInstance(InstanceType<? extends ShadowInstance> type, InstanceHandle handle) {
        super(type, handle);
    }
}

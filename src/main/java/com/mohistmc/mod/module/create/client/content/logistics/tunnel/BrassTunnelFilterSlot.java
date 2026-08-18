package com.mohistmc.mod.module.create.client.content.logistics.tunnel;

import com.mohistmc.mod.module.create.catnip.math.VecHelper;
import com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.minecraft.world.phys.Vec3;

public class BrassTunnelFilterSlot extends ValueBoxTransform.Sided {

    @Override
    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace(8, 13, 15.5f);
    }

}

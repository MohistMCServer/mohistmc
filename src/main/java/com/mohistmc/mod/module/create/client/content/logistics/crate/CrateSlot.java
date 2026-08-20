package com.mohistmc.mod.module.create.client.content.logistics.crate;

import com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.mohistmc.mod.module.flywheel.lib.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CrateSlot extends ValueBoxTransform {
    @Override
    public void rotate(BlockState state, PoseStack ms) {
        TransformStack.of(ms).rotateXDegrees(90);
    }

    @Override
    public Vec3 getLocalOffset(BlockState state) {
        return new Vec3(0.5, 13.5 / 16.0d, 0.5);
    }
}

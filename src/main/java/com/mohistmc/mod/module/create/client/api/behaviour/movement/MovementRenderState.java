package com.mohistmc.mod.module.create.client.api.behaviour.movement;

import com.mohistmc.mod.module.create.client.catnip.render.SuperByteBuffer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.BlockPos;

public interface MovementRenderState {
    default void transform(PoseStack matrices, Pose pose, BlockPos pos) {
        Pose entry = matrices.last();
        SuperByteBuffer.mul(entry, pose);
        entry.translate(pos.getX(), pos.getY(), pos.getZ());
    }

    void submit(PoseStack matrices, SubmitNodeCollector queue);
}

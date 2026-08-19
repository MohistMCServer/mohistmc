package com.mohistmc.mod.module.flywheel.lib.model.baked;

import com.mojang.blaze3d.vertex.PoseStack;

public interface BufferPoseEmitter extends BufferEmitter {
    PoseStack.Pose getPose();
}

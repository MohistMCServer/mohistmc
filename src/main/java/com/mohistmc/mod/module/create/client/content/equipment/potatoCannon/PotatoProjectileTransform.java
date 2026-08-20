package com.mohistmc.mod.module.create.client.content.equipment.potatoCannon;

import com.mohistmc.mod.module.create.api.equipment.potatoCannon.PotatoProjectileRenderMode;
import com.mohistmc.mod.module.create.client.content.equipment.potatoCannon.PotatoProjectileRenderer.PotatoProjectileState;
import com.mojang.blaze3d.vertex.PoseStack;

@FunctionalInterface
public interface PotatoProjectileTransform<T extends PotatoProjectileRenderMode> {
    void transform(T mode, PoseStack ms, PotatoProjectileState state);
}

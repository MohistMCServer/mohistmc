package com.mohistmc.mod.module.create.client.content.equipment.potatoCannon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mohistmc.mod.module.create.api.equipment.potatoCannon.PotatoProjectileRenderMode;
import com.mohistmc.mod.module.create.client.content.equipment.potatoCannon.PotatoProjectileRenderer.PotatoProjectileState;

@FunctionalInterface
public interface PotatoProjectileTransform<T extends PotatoProjectileRenderMode> {
    void transform(T mode, PoseStack ms, PotatoProjectileState state);
}

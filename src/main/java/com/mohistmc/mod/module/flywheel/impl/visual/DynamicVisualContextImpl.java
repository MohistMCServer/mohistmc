package com.mohistmc.mod.module.flywheel.impl.visual;

import com.mohistmc.mod.module.flywheel.api.visual.DistanceUpdateLimiter;
import com.mohistmc.mod.module.flywheel.api.visual.DynamicVisual;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.FrustumIntersection;

public record DynamicVisualContextImpl(CameraRenderState camera, FrustumIntersection frustum, float partialTick,
                                       DistanceUpdateLimiter limiter) implements DynamicVisual.Context {
}

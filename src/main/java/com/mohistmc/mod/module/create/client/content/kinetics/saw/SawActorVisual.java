package com.mohistmc.mod.module.create.client.content.kinetics.saw;

import com.mohistmc.mod.module.create.client.content.contraptions.render.ActorVisual;
import com.mohistmc.mod.module.create.client.content.kinetics.base.KineticBlockEntityVisual;
import com.mohistmc.mod.module.create.client.content.kinetics.base.RotatingInstance;
import com.mohistmc.mod.module.create.client.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.create.client.foundation.virtualWorld.VirtualRenderWorld;
import com.mohistmc.mod.module.create.content.contraptions.behaviour.MovementContext;

public class SawActorVisual extends ActorVisual {
    private final RotatingInstance shaft;

    public SawActorVisual(
        VisualizationContext visualizationContext,
        VirtualRenderWorld simulationWorld,
        MovementContext movementContext
    ) {
        super(visualizationContext, simulationWorld, movementContext);

        var state = movementContext.state;
        var localPos = movementContext.localPos;
        shaft = SawVisual.shaft(instancerProvider, state);

        var axis = KineticBlockEntityVisual.rotationAxis(state);
        shaft.setRotationAxis(axis).setRotationOffset(KineticBlockEntityVisual.rotationOffset(state, axis, localPos))
            .setPosition(localPos).light(localBlockLight(), 0).setChanged();
    }

    @Override
    protected void _delete() {
        shaft.delete();
    }
}

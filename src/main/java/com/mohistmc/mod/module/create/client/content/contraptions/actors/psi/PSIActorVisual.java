package com.mohistmc.mod.module.create.client.content.contraptions.actors.psi;

import com.mohistmc.mod.module.create.catnip.animation.LerpedFloat;
import com.mohistmc.mod.module.create.client.catnip.animation.AnimationTickHolder;
import com.mohistmc.mod.module.create.client.content.contraptions.render.ActorVisual;
import com.mohistmc.mod.module.create.client.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.create.client.foundation.virtualWorld.VirtualRenderWorld;
import com.mohistmc.mod.module.create.content.contraptions.actors.psi.PortableStorageInterfaceMovement;
import com.mohistmc.mod.module.create.content.contraptions.behaviour.MovementContext;

public class PSIActorVisual extends ActorVisual {

    private final PIInstance instance;

    public PSIActorVisual(VisualizationContext context, VirtualRenderWorld world, MovementContext movementContext) {
        super(context, world, movementContext);

        instance = new PIInstance(context.instancerProvider(), movementContext.state, movementContext.localPos, false);

        instance.middle.light(localBlockLight(), 0);
        instance.top.light(localBlockLight(), 0);
    }

    @Override
    public void beginFrame() {
        LerpedFloat lf = PortableStorageInterfaceMovement.getAnimation(context);
        instance.tick(lf.settled());
        instance.beginFrame(lf.getValue(AnimationTickHolder.getPartialTicks()));
    }

    @Override
    protected void _delete() {
        instance.remove();
    }
}

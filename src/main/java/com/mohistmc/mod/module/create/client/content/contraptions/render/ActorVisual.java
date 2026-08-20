package com.mohistmc.mod.module.create.client.content.contraptions.render;

import com.mohistmc.mod.module.create.content.contraptions.behaviour.MovementContext;
import com.mohistmc.mod.module.flywheel.api.instance.InstancerProvider;
import com.mohistmc.mod.module.flywheel.api.visual.Visual;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;

public abstract class ActorVisual implements Visual {
    protected final VisualizationContext visualizationContext;
    protected final InstancerProvider instancerProvider;
    protected final BlockAndTintGetter simulationWorld;
    protected final MovementContext context;

    private boolean deleted;

    public ActorVisual(VisualizationContext visualizationContext, BlockAndTintGetter world, MovementContext context) {
        this.visualizationContext = visualizationContext;
        instancerProvider = visualizationContext.instancerProvider();
        simulationWorld = world;
        this.context = context;
    }

    public void tick() {
    }

    public void beginFrame() {
    }

    protected int localBlockLight() {
        return simulationWorld.getBrightness(LightLayer.BLOCK, context.localPos);
    }

    @Override
    public void update(float partialTick) {
    }

    protected abstract void _delete();

    @Override
    public final void delete() {
        if (deleted) {
            return;
        }

        _delete();
        deleted = true;
    }
}

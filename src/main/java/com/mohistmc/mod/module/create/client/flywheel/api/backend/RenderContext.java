package com.mohistmc.mod.module.create.client.flywheel.api.backend;

import com.zurrtum.create.client.flywheel.api.backend.Engine.CrumblingBlock;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import java.util.List;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.joml.Matrix4fc;

public interface RenderContext {
    LevelRenderState levelRenderState();

    Matrix4fc projection();

    Matrix4fc viewProjection();

    float partialTick();

    VisualizationManager visualizationManager();

    List<CrumblingBlock> crumblingBlocks();
}

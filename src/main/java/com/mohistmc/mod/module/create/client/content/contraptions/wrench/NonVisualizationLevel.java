package com.mohistmc.mod.module.create.client.content.contraptions.wrench;

import com.mohistmc.mod.module.create.catnip.levelWrappers.WrappedLevel;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationLevel;
import net.minecraft.world.level.Level;

public class NonVisualizationLevel extends WrappedLevel implements VisualizationLevel {
    public NonVisualizationLevel(Level level) {
        super(level);
    }

    @Override
    public boolean supportsVisualization() {
        return false;
    }
}

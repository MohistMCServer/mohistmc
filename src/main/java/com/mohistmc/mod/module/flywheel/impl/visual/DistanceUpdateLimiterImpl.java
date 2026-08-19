package com.mohistmc.mod.module.flywheel.impl.visual;

import com.mohistmc.mod.module.flywheel.api.visual.DistanceUpdateLimiter;

public interface DistanceUpdateLimiterImpl extends DistanceUpdateLimiter {
    /**
     * Call this before every update.
     */
    void tick();
}

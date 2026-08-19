package com.mohistmc.mod.module.ponder.api;

import com.mohistmc.mod.module.ponder.api.level.PonderLevel;

@FunctionalInterface
public interface ParticleEmitter {
    void create(PonderLevel world, double x, double y, double z);
}
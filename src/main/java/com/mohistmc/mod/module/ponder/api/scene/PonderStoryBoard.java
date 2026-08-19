package com.mohistmc.mod.module.ponder.api.scene;

@FunctionalInterface
public interface PonderStoryBoard {
    void program(SceneBuilder scene, SceneBuildingUtil util);
}
package com.mohistmc.mod.module.create.client.flywheel.lib.model;

import com.mohistmc.mod.module.create.client.flywheel.api.model.IndexSequence;
import com.mohistmc.mod.module.create.client.flywheel.api.model.Mesh;

public interface QuadMesh extends Mesh {
    @Override
    default IndexSequence indexSequence() {
        return QuadIndexSequence.INSTANCE;
    }

    @Override
    default int indexCount() {
        return vertexCount() / 2 * 3;
    }
}

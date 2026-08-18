package com.mohistmc.mod.module.create.client.flywheel.backend.engine.embed;

import com.mohistmc.mod.module.create.client.flywheel.backend.compile.ContextShader;
import com.mohistmc.mod.module.create.client.flywheel.backend.gl.shader.GlProgram;

public interface Environment {
    ContextShader contextShader();

    void setupDraw(GlProgram drawProgram);

    int matrixIndex();
}

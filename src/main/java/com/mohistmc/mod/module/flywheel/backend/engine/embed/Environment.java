package com.mohistmc.mod.module.flywheel.backend.engine.embed;

import com.mohistmc.mod.module.flywheel.backend.compile.ContextShader;
import com.mohistmc.mod.module.flywheel.backend.gl.shader.GlProgram;

public interface Environment {
    ContextShader contextShader();

    void setupDraw(GlProgram drawProgram);

    int matrixIndex();
}

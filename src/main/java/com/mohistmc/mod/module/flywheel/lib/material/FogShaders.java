package com.mohistmc.mod.module.flywheel.lib.material;

import com.mohistmc.mod.module.flywheel.api.material.FogShader;
import com.mohistmc.mod.module.flywheel.lib.util.ResourceUtil;

public final class FogShaders {
    public static final FogShader NONE = new SimpleFogShader(ResourceUtil.rl("fog/none.glsl"));
    public static final FogShader LINEAR = new SimpleFogShader(ResourceUtil.rl("fog/linear.glsl"));
    public static final FogShader LINEAR_FADE = new SimpleFogShader(ResourceUtil.rl("fog/linear_fade.glsl"));

    private FogShaders() {
    }
}

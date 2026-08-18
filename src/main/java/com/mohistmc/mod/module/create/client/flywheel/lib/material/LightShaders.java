package com.mohistmc.mod.module.create.client.flywheel.lib.material;

import com.mohistmc.mod.module.create.client.flywheel.api.material.LightShader;
import com.mohistmc.mod.module.create.client.flywheel.lib.util.ResourceUtil;

public final class LightShaders {
    public static final LightShader SMOOTH_WHEN_EMBEDDED = new SimpleLightShader(ResourceUtil.rl(
        "light/smooth_when_embedded.glsl"));
    public static final LightShader SMOOTH = new SimpleLightShader(ResourceUtil.rl("light/smooth.glsl"));
    public static final LightShader FLAT = new SimpleLightShader(ResourceUtil.rl("light/flat.glsl"));

    private LightShaders() {
    }
}

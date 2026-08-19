package com.mohistmc.mod.module.flywheel.lib.material;

import com.mohistmc.mod.module.flywheel.api.material.MaterialShaders;
import com.mohistmc.mod.module.flywheel.lib.util.ResourceUtil;

public final class StandardMaterialShaders {
    public static final MaterialShaders DEFAULT = new SimpleMaterialShaders(
        ResourceUtil.rl("material/default.vert"),
        ResourceUtil.rl("material/default.frag")
    );

    public static final MaterialShaders WIREFRAME = new SimpleMaterialShaders(
        ResourceUtil.rl("material/wireframe.vert"),
        ResourceUtil.rl("material/wireframe.frag")
    );

    public static final MaterialShaders LINE = new SimpleMaterialShaders(
        ResourceUtil.rl("material/lines.vert"),
        ResourceUtil.rl("material/lines.frag")
    );

    public static final MaterialShaders GLINT = new SimpleMaterialShaders(
        ResourceUtil.rl("material/glint.vert"),
        ResourceUtil.rl("material/default.frag")
    );

    private StandardMaterialShaders() {
    }
}

package com.mohistmc.mod.module.create.client.flywheel.backend.engine;

import com.mohistmc.mod.module.create.client.flywheel.api.material.CardinalLightingMode;
import com.mohistmc.mod.module.create.client.flywheel.api.material.Material;
import com.mohistmc.mod.module.create.client.flywheel.api.material.Transparency;
import com.mohistmc.mod.module.create.client.flywheel.api.material.WriteMask;
import com.mohistmc.mod.module.create.client.flywheel.lib.material.CutoutShaders;
import com.mohistmc.mod.module.create.client.flywheel.lib.material.FogShaders;
import com.mohistmc.mod.module.create.client.flywheel.lib.material.LightShaders;
import com.mohistmc.mod.module.create.client.flywheel.lib.material.SimpleMaterial;

public class CommonCrumbling {
    public static void applyCrumblingProperties(SimpleMaterial.Builder crumblingMaterial, Material baseMaterial) {
        crumblingMaterial.copyFrom(baseMaterial).fog(FogShaders.NONE).cutout(CutoutShaders.ONE_TENTH)
            .light(LightShaders.SMOOTH_WHEN_EMBEDDED).polygonOffset(true).transparency(Transparency.CRUMBLING)
            .writeMask(WriteMask.COLOR).useOverlay(false).useLight(false)
            .cardinalLightingMode(CardinalLightingMode.OFF);
    }
}

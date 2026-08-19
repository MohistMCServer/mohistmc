package com.mohistmc.mod.module.flywheel.lib.material;

import com.mohistmc.mod.module.flywheel.api.material.FogShader;
import net.minecraft.resources.Identifier;

public record SimpleFogShader(@Override Identifier source) implements FogShader {
}

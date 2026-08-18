package com.mohistmc.mod.module.create.client.flywheel.lib.material;

import com.mohistmc.mod.module.create.client.flywheel.api.material.FogShader;
import net.minecraft.resources.Identifier;

public record SimpleFogShader(@Override Identifier source) implements FogShader {
}

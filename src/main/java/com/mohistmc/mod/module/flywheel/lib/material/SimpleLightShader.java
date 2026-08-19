package com.mohistmc.mod.module.flywheel.lib.material;

import com.mohistmc.mod.module.flywheel.api.material.LightShader;
import net.minecraft.resources.Identifier;

public record SimpleLightShader(@Override Identifier source) implements LightShader {
}

package com.mohistmc.mod.module.create.client.flywheel.lib.material;

import com.mohistmc.mod.module.create.client.flywheel.api.material.LightShader;
import net.minecraft.resources.Identifier;

public record SimpleLightShader(@Override Identifier source) implements LightShader {
}

package com.mohistmc.mod.module.flywheel.lib.material;

import com.mohistmc.mod.module.flywheel.api.material.MaterialShaders;
import net.minecraft.resources.Identifier;

public record SimpleMaterialShaders(Identifier vertexSource, Identifier fragmentSource) implements MaterialShaders {
}

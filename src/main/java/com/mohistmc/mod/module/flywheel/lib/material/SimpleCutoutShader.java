package com.mohistmc.mod.module.flywheel.lib.material;

import com.mohistmc.mod.module.flywheel.api.material.CutoutShader;
import net.minecraft.resources.Identifier;

public record SimpleCutoutShader(@Override Identifier source) implements CutoutShader {
}

package com.mohistmc.mod.mixin.create.client;

import com.mohistmc.mod.module.create.client.foundation.model.BakedModelHelper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BakedModelHelper.class)
public class BakedModelHelperMixin {
    @Overwrite(remap = false)
    public static void setNormals(BakedQuad quad, Vector3f[] normals) {
    }

    @Overwrite(remap = false)
    public static void setNormals(BakedQuad quad, BakedQuad target) {
    }
}

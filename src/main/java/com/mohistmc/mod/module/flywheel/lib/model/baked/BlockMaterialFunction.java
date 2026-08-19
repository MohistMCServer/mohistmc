package com.mohistmc.mod.module.flywheel.lib.model.baked;

import com.mohistmc.mod.module.flywheel.api.material.Material;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import org.jspecify.annotations.Nullable;

public interface BlockMaterialFunction {
    @Nullable Material apply(ChunkSectionLayer chunkRenderType, boolean shaded, boolean ambientOcclusion);
}

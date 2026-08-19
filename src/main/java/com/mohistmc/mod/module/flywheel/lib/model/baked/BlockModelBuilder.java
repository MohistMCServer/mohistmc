package com.mohistmc.mod.module.flywheel.lib.model.baked;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mohistmc.mod.module.flywheel.api.material.Material;
import com.mohistmc.mod.module.flywheel.lib.internal.FlwLibXplat;
import com.mohistmc.mod.module.flywheel.lib.model.ModelUtil;
import com.mohistmc.mod.module.flywheel.lib.model.SimpleModel;
import java.util.function.BiFunction;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;

public final class BlockModelBuilder {
    final BlockAndTintGetter level;
    final Iterable<BlockPos> positions;
    @Nullable PoseStack poseStack;
    boolean renderFluids;
    @Nullable BlockMaterialFunction materialFunc;

    public BlockModelBuilder(BlockAndTintGetter level, Iterable<BlockPos> positions) {
        this.level = level;
        this.positions = positions;
    }

    public BlockModelBuilder poseStack(@Nullable PoseStack poseStack) {
        this.poseStack = poseStack;
        return this;
    }

    public BlockModelBuilder renderFluids(boolean renderFluids) {
        this.renderFluids = renderFluids;
        return this;
    }

    @Deprecated(forRemoval = true)
    public BlockModelBuilder materialFunc(@Nullable BiFunction<ChunkSectionLayer, Boolean, @Nullable Material> materialFunc) {
        if (materialFunc != null) {
            this.materialFunc = (chunkRenderType, shaded, ambientOcclusion) -> materialFunc.apply(
                chunkRenderType,
                shaded
            );
        } else {
            this.materialFunc = null;
        }
        return this;
    }

    public BlockModelBuilder materialFunc(@Nullable BlockMaterialFunction materialFunc) {
        this.materialFunc = materialFunc;
        return this;
    }

    public SimpleModel build() {
        if (materialFunc == null) {
            materialFunc = ModelUtil::getMaterial;
        }

        return FlwLibXplat.INSTANCE.buildBlockModelBuilder(this);
    }
}
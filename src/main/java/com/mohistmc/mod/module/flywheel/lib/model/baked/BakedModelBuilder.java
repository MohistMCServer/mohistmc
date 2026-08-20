package com.mohistmc.mod.module.flywheel.lib.model.baked;

import com.mohistmc.mod.module.flywheel.api.material.Material;
import com.mohistmc.mod.module.flywheel.lib.internal.FlwLibXplat;
import com.mohistmc.mod.module.flywheel.lib.model.ModelUtil;
import com.mohistmc.mod.module.flywheel.lib.model.SimpleModel;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.BiFunction;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;

public final class BakedModelBuilder {
    final BlockStateModel model;
    @Nullable BlockAndTintGetter level;
    @Nullable BlockPos pos;
    @Nullable PoseStack poseStack;
    @Nullable BlockMaterialFunction materialFunc;

    public BakedModelBuilder(BlockStateModel model) {
        this.model = model;
    }

    public BakedModelBuilder level(@Nullable BlockAndTintGetter level) {
        this.level = level;
        return this;
    }

    public BakedModelBuilder pos(@Nullable BlockPos pos) {
        this.pos = pos;
        return this;
    }

    public BakedModelBuilder poseStack(@Nullable PoseStack poseStack) {
        this.poseStack = poseStack;
        return this;
    }

    @Deprecated(forRemoval = true)
    public BakedModelBuilder materialFunc(@Nullable BiFunction<ChunkSectionLayer, Boolean, @Nullable Material> materialFunc) {
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

    public BakedModelBuilder materialFunc(@Nullable BlockMaterialFunction materialFunc) {
        this.materialFunc = materialFunc;
        return this;
    }

    public SimpleModel build() {
        if (level == null) {
            level = EmptyVirtualBlockGetter.FULL_DARK;
        }
        if (pos == null) {
            pos = BlockPos.ZERO;
        }
        if (materialFunc == null) {
            materialFunc = ModelUtil::getMaterial;
        }

        return FlwLibXplat.INSTANCE.buildBakedModelBuilder(this);
    }
}
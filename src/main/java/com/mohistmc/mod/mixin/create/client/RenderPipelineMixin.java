package com.mohistmc.mod.mixin.create.client;

import com.mohistmc.mod.module.create.client.foundation.render.CustomRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RenderPipeline.class)
public class RenderPipelineMixin implements CustomRenderPipeline {
    @Unique
    private boolean solidBlend;

    @Override
    public boolean create$isSolidBlend() {
        return solidBlend;
    }

    @Override
    public void create$markSolidBlend() {
        solidBlend = true;
    }
}

package com.mohistmc.mod.client.renderer;

import com.mohistmc.mod.entity.BulletEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

/**
 * @author Mgazul
 * @date 2026/3/31
 */
public class BulletRenderer extends EntityRenderer<BulletEntity, ThrownItemRenderState> {
    private final ItemModelResolver itemModelResolver;

    public BulletRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public void extractRenderState(@NonNull BulletEntity entity, @NonNull ThrownItemRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        this.itemModelResolver.updateForNonLiving(state.item, Items.SNOWBALL.getDefaultInstance(), ItemDisplayContext.GROUND, entity);
    }


    @Override
    public void submit(ThrownItemRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.scale(0.01F, 0.01F, 0.01F);
        poseStack.mulPose(camera.orientation);
        state.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    protected int getBlockLightLevel(BulletEntity entity, BlockPos pos) {
        Level level = entity.level();
        return level.isClientSide() && level.hasChunkAt(pos.getX() >> 4, pos.getZ() >> 4)
                ? level.getMaxLocalRawBrightness(pos)
                : 0;
    }

    @Override
    public ThrownItemRenderState createRenderState() {
        return new ThrownItemRenderState();
    }
}

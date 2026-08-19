package com.mohistmc.mod.module.create.client.content.fluids.pipes;

import com.mohistmc.mod.module.create.api.behaviour.BlockEntityBehaviour;
import com.mohistmc.mod.module.create.catnip.animation.LerpedFloat;
import com.mohistmc.mod.module.create.catnip.data.Iterate;
import com.mohistmc.mod.module.create.client.AllFluidConfigs;
import com.mohistmc.mod.module.create.client.content.fluids.FluidInstance;
import com.mohistmc.mod.module.create.client.content.fluids.FluidMesh;
import com.mohistmc.mod.module.flywheel.api.instance.Instance;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.lib.instance.InstanceTypes;
import com.mohistmc.mod.module.flywheel.lib.instance.TransformedInstance;
import com.mohistmc.mod.module.flywheel.lib.transform.Translate;
import com.mohistmc.mod.module.flywheel.lib.visual.AbstractBlockEntityVisual;
import com.mohistmc.mod.module.flywheel.lib.visual.SimpleDynamicVisual;
import com.mohistmc.mod.module.flywheel.lib.visual.util.SmartRecycler;
import com.mohistmc.mod.module.create.client.foundation.render.AllInstanceTypes;
import com.mohistmc.mod.module.create.content.fluids.FluidTransportBehaviour;
import com.mohistmc.mod.module.create.content.fluids.PipeConnection.Flow;
import com.mohistmc.mod.module.create.content.fluids.pipes.StraightPipeBlockEntity;
import com.mohistmc.mod.module.create.infrastructure.fluids.FluidStack;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;

public class GlassPipeVisual extends AbstractBlockEntityVisual<StraightPipeBlockEntity> implements SimpleDynamicVisual {

    private int light;

    private final SmartRecycler<TextureAtlasSprite, FluidInstance> stream;
    private final SmartRecycler<TextureAtlasSprite, TransformedInstance> surface;

    public GlassPipeVisual(VisualizationContext ctx, StraightPipeBlockEntity blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick);

        stream = new SmartRecycler<>(sprite -> ctx.instancerProvider()
            .instancer(AllInstanceTypes.FLUID, FluidMesh.stream(sprite)).createInstance());
        surface = new SmartRecycler<>(sprite -> ctx.instancerProvider()
            .instancer(InstanceTypes.TRANSFORMED, FluidMesh.surface(sprite, FluidMesh.PIPE_RADIUS)).createInstance());
    }

    @Override
    public void beginFrame(Context ctx) {
        stream.resetCount();
        surface.resetCount();

        FluidTransportBehaviour pipe = blockEntity.getBehaviour(FluidTransportBehaviour.TYPE);
        if (pipe == null) {
            stream.discardExtra();
            surface.discardExtra();
            return;
        }

        FluidStateModelSet fluidStateModelSet = Minecraft.getInstance().getModelManager().getFluidStateModelSet();
        for (Direction side : Iterate.directions) {

            Flow flow = pipe.getFlow(side);
            if (flow == null) {
                continue;
            }
            FluidStack fluidStack = flow.fluid;
            if (fluidStack.isEmpty()) {
                continue;
            }
            LerpedFloat progressLerp = flow.progress;
            if (progressLerp == null) {
                continue;
            }

            float progress = progressLerp.getValue(ctx.partialTick());
            boolean inbound = flow.inbound;
            if (progress == 1) {
                if (inbound) {
                    Flow opposite = pipe.getFlow(side.getOpposite());
                    if (opposite == null) {
                        progress -= 1.0e-6f;
                    }
                } else {
                    FluidTransportBehaviour adjacent = BlockEntityBehaviour.get(
                        level,
                        pos.relative(side),
                        FluidTransportBehaviour.TYPE
                    );
                    if (adjacent == null) {
                        progress -= 1.0e-6f;
                    } else {
                        Flow other = adjacent.getFlow(side.getOpposite());
                        if (other == null || !other.inbound && !other.complete) {
                            progress -= 1.0e-6f;
                        }
                    }
                }
            }

            Fluid fluid = fluidStack.getFluid();
            FluidState fluidState = fluid.defaultFluidState();
            BlockState blockState = fluidState.createLegacyBlock();
            FluidModel model = fluidStateModelSet.get(fluidState);
            TextureAtlasSprite flowTexture = model.flowingMaterial().sprite();

            int tint = AllFluidConfigs.getTint(
                (BlockAndTintGetter) level,
                pos,
                blockState,
                model,
                fluid,
                fluidStack.getComponentChanges()
            ) | 0xff000000;
            int blockLightIn = light >> 4 & 0xF;
            int luminosity = Math.max(blockLightIn, blockState.getLightEmission());
            int light = this.light & 0xF00000 | luminosity << 4;

            if (inbound) {
                side = side.getOpposite();
            }

            var yStart = inbound ? 0 : 0.5f;
            var progressOffset = Mth.clamp(progress * 0.5f, 0, 1);

            var fluidInstance = stream.get(flowTexture);

            fluidInstance.setIdentityTransform().translate(getVisualPosition()).center().rotateTo(Direction.UP, side)
                .translate(0, -Translate.CENTER + yStart, 0);

            fluidInstance.light(light).colorArgb(tint);


            fluidInstance.vScale = (flowTexture.getV1() - flowTexture.getV0()) * 0.5f;
            fluidInstance.v0 = flowTexture.getV0() + yStart * fluidInstance.vScale;
            fluidInstance.progress = progressOffset;

            fluidInstance.setChanged();

            if (progress != 1) {
                TextureAtlasSprite stillTexture = model.stillMaterial().sprite();
                surface.get(stillTexture).setIdentityTransform().translate(getVisualPosition()).center()
                    .rotateTo(Direction.UP, side).translate(0, -Translate.CENTER + yStart + progressOffset, 0)
                    .light(light).colorArgb(tint).setChanged();
            }
        }

        stream.discardExtra();
        surface.discardExtra();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
    }

    @Override
    public void updateLight(float partialTick) {
        light = computePackedLight();
    }

    @Override
    protected void _delete() {
        stream.delete();
        surface.delete();
    }

}

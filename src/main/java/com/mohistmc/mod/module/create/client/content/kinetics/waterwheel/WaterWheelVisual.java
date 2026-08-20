package com.mohistmc.mod.module.create.client.content.kinetics.waterwheel;

import com.mohistmc.mod.module.create.client.content.kinetics.base.KineticBlockEntityVisual;
import com.mohistmc.mod.module.create.client.content.kinetics.base.RotatingInstance;
import com.mohistmc.mod.module.create.client.content.kinetics.waterwheel.WaterWheelRenderer.Variant;
import com.mohistmc.mod.module.create.client.foundation.render.AllInstanceTypes;
import com.mohistmc.mod.module.create.content.kinetics.waterwheel.WaterWheelBlockEntity;
import com.mohistmc.mod.module.flywheel.api.instance.Instance;
import com.mohistmc.mod.module.flywheel.api.model.Model;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.lib.model.ModelUtil;
import com.mohistmc.mod.module.flywheel.lib.model.baked.BakedModelBuilder;
import com.mohistmc.mod.module.flywheel.lib.util.RendererReloadCache;
import java.util.function.Consumer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class WaterWheelVisual<T extends WaterWheelBlockEntity> extends KineticBlockEntityVisual<T> {
    private static final RendererReloadCache<ModelKey, Model> MODEL_CACHE = new RendererReloadCache<>(WaterWheelVisual::createModel);

    protected final boolean large;
    protected BlockState lastMaterial;
    protected RotatingInstance rotatingModel;

    public WaterWheelVisual(VisualizationContext context, T blockEntity, boolean large, float partialTick) {
        super(context, blockEntity, partialTick);
        this.large = large;

        setupInstance();
    }

    @Override
    public void setSectionCollector(SectionCollector sectionCollector) {
        if (large) {
            switch (blockState.getValue(BlockStateProperties.AXIS)) {
                case X -> setSectionCollector(sectionCollector, 0, -2, -2, 0, 2, 2);
                case Y -> setSectionCollector(sectionCollector, -2, 0, -2, 2, 0, 2);
                default -> setSectionCollector(sectionCollector, -2, -2, 0, 2, 2, 0);
            }
        } else {
            switch (blockState.getValue(BlockStateProperties.FACING).getAxis()) {
                case X -> setSectionCollector(sectionCollector, 0, -1, -1, 0, 1, 1);
                case Y -> setSectionCollector(sectionCollector, -1, 0, -1, 1, 0, 1);
                default -> setSectionCollector(sectionCollector, -1, -1, 0, 1, 1, 0);
            }
        }
    }

    public static <T extends WaterWheelBlockEntity> WaterWheelVisual<T> standard(
        VisualizationContext context,
        T blockEntity,
        float partialTick
    ) {
        return new WaterWheelVisual<>(context, blockEntity, false, partialTick);
    }

    public static <T extends WaterWheelBlockEntity> WaterWheelVisual<T> large(
        VisualizationContext context,
        T blockEntity,
        float partialTick
    ) {
        return new WaterWheelVisual<>(context, blockEntity, true, partialTick);
    }

    private void setupInstance() {
        lastMaterial = blockEntity.material;
        rotatingModel = instancerProvider().instancer(
            AllInstanceTypes.ROTATING,
            MODEL_CACHE.get(new ModelKey(Variant.of(large, blockState), blockEntity.material))
        ).createInstance();
        rotatingModel.setup(blockEntity).setPosition(getVisualPosition()).rotateToFace(rotationAxis()).setChanged();
    }

    @Override
    public void update(float pt) {
        if (lastMaterial != blockEntity.material) {
            rotatingModel.delete();
            setupInstance();
        } else {
            rotatingModel.setup(blockEntity).setChanged();
        }
    }

    @Override
    public void updateLight(float partialTick) {
        relight(rotatingModel);
    }

    @Override
    protected void _delete() {
        rotatingModel.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(rotatingModel);
    }

    private static Model createModel(ModelKey key) {
        BlockStateModel model = WaterWheelRenderer.generateModel(key.variant(), key.material());
        return new BakedModelBuilder(model).materialFunc(ModelUtil::getChunkMaterial).build();
    }

    public record ModelKey(WaterWheelRenderer.Variant variant, BlockState material) {
    }
}

package com.mohistmc.mod.module.create.client.content.contraptions.actors.psi;

import com.mohistmc.mod.module.create.catnip.math.AngleHelper;
import com.mohistmc.mod.module.flywheel.api.instance.Instance;
import com.mohistmc.mod.module.flywheel.api.instance.InstancerProvider;
import com.mohistmc.mod.module.flywheel.lib.instance.InstanceTypes;
import com.mohistmc.mod.module.flywheel.lib.instance.TransformedInstance;
import com.mohistmc.mod.module.flywheel.lib.model.Models;
import com.mohistmc.mod.module.create.content.contraptions.actors.psi.PortableStorageInterfaceBlock;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class PIInstance {
    private final InstancerProvider instancerProvider;
    private final BlockState blockState;
    private final BlockPos instancePos;
    private final float angleX;
    private final float angleY;

    private boolean lit;
    TransformedInstance middle;
    TransformedInstance top;

    public PIInstance(InstancerProvider instancerProvider, BlockState blockState, BlockPos instancePos, boolean lit) {
        this.instancerProvider = instancerProvider;
        this.blockState = blockState;
        this.instancePos = instancePos;
        Direction facing = blockState.getValue(PortableStorageInterfaceBlock.FACING);
        angleX = facing == Direction.UP ? 0 : facing == Direction.DOWN ? 180 : 90;
        angleY = AngleHelper.horizontalAngle(facing);
        this.lit = lit;

        middle = instancerProvider.instancer(
            InstanceTypes.TRANSFORMED,
            Models.chunkPartial(PortableStorageInterfaceRenderer.getMiddleForState(blockState, lit))
        ).createInstance();
        top = instancerProvider.instancer(
            InstanceTypes.TRANSFORMED,
            Models.chunkPartial(PortableStorageInterfaceRenderer.getTopForState(blockState))
        ).createInstance();
    }

    public void beginFrame(float progress) {
        middle.setIdentityTransform().translate(instancePos).center().rotateYDegrees(angleY).rotateXDegrees(angleX)
            .uncenter();

        top.setIdentityTransform().translate(instancePos).center().rotateYDegrees(angleY).rotateXDegrees(angleX)
            .uncenter();

        middle.translate(0, progress * 0.5f + 0.375f, 0);
        top.translate(0, progress, 0);

        middle.setChanged();
        top.setChanged();
    }

    public void tick(boolean lit) {
        if (this.lit != lit) {
            this.lit = lit;
            instancerProvider.instancer(
                InstanceTypes.TRANSFORMED,
                Models.chunkPartial(PortableStorageInterfaceRenderer.getMiddleForState(blockState, lit))
            ).stealInstance(middle);
        }
    }

    public void remove() {
        middle.delete();
        top.delete();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(middle);
        consumer.accept(top);
    }
}

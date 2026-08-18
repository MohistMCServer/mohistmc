package com.mohistmc.mod.module.create.client.content.contraptions.mounted;

import com.mohistmc.mod.module.create.catnip.math.VecHelper;
import com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.mohistmc.mod.module.create.content.contraptions.mounted.CartAssemblerBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

public class CartAssemblerValueBoxTransform extends CenteredSideValueBoxTransform {
    public CartAssemblerValueBoxTransform() {
        super((state, d) -> {
            if (d.getAxis().isVertical()) {
                return false;
            }
            if (!state.hasProperty(CartAssemblerBlock.RAIL_SHAPE)) {
                return false;
            }
            RailShape railShape = state.getValue(CartAssemblerBlock.RAIL_SHAPE);
            return (d.getAxis() == Direction.Axis.X) == (railShape == RailShape.NORTH_SOUTH);
        });
    }

    @Override
    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace(8, 7, 17.5);
    }
}

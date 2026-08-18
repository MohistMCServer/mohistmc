package com.mohistmc.mod.module.create.client.content.logistics.itemHatch;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mohistmc.mod.module.create.catnip.math.AngleHelper;
import com.mohistmc.mod.module.create.catnip.math.VecHelper;
import com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.mohistmc.mod.module.create.content.logistics.itemHatch.ItemHatchBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class HatchFilterSlot extends ValueBoxTransform {

    @Override
    public Vec3 getLocalOffset(BlockState state) {
        return VecHelper.rotateCentered(VecHelper.voxelSpace(8, 5.15, 9.5), angle(state), Direction.Axis.Y);
    }

    @Override
    public float getScale() {
        return super.getScale() * 0.965f;
    }

    @Override
    public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
        return localHit.distanceTo(getLocalOffset(state).subtract(0, 0.125, 0)) < scale / 2;
    }

    @Override
    public void rotate(BlockState state, PoseStack ms) {
        ms.mulPose(Axis.YP.rotationDegrees(angle(state)));
        ms.mulPose(Axis.XP.rotationDegrees(-45));
    }

    private float angle(BlockState state) {
        return AngleHelper.horizontalAngle(state.getValueOrElse(ItemHatchBlock.FACING, Direction.NORTH));
    }

}

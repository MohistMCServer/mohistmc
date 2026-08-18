package com.mohistmc.mod.module.create.content.kinetics.crusher;

import com.mohistmc.mod.module.create.AllAdvancements;
import com.mohistmc.mod.module.create.AllBlockEntityTypes;
import com.mohistmc.mod.module.create.catnip.data.Iterate;
import com.mohistmc.mod.module.create.content.kinetics.base.KineticBlockEntity;
import com.mohistmc.mod.module.create.foundation.advancement.CreateTrigger;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class CrushingWheelBlockEntity extends KineticBlockEntity {
    public CrushingWheelBlockEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntityTypes.CRUSHING_WHEEL, pos, state);
        setLazyTickRate(20);
    }

    @Override
    public List<CreateTrigger> getAwardables() {
        return List.of(AllAdvancements.CRUSHING_WHEEL, AllAdvancements.CRUSHER_MAXED);
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        fixControllers();
    }

    public void fixControllers() {
        for (Direction d : Iterate.directions) {
            ((CrushingWheelBlock) getBlockState().getBlock()).updateControllers(
                getBlockState(),
                getLevel(),
                getBlockPos(),
                d
            );
        }
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(worldPosition).inflate(1);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        fixControllers();
    }
}

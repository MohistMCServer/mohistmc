package com.mohistmc.mod.module.create.content.kinetics.gearbox;

import com.mohistmc.mod.module.create.AllBlockEntityTypes;
import com.mohistmc.mod.module.create.AllItems;
import com.mohistmc.mod.module.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.mohistmc.mod.module.create.foundation.block.IBE;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams.Builder;

public class GearboxBlock extends RotatedPillarKineticBlock implements IBE<GearboxBlockEntity> {

    public GearboxBlock(Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, Builder builder) {
        if (state.getValue(AXIS).isVertical()) {
            return super.getDrops(state, builder);
        }
        return List.of(new ItemStack(AllItems.VERTICAL_GEARBOX));
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        if (state.getValue(AXIS).isVertical()) {
            return super.getCloneItemStack(world, pos, state, includeData);
        }
        return AllItems.VERTICAL_GEARBOX.getDefaultInstance();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(AXIS, Axis.Y);
    }

    // IRotate:

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() != state.getValue(AXIS);
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public Class<GearboxBlockEntity> getBlockEntityClass() {
        return GearboxBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends GearboxBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.GEARBOX;
    }
}
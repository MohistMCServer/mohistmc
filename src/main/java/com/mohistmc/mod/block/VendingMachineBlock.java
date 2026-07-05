package com.mohistmc.mod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * @author Mgazul
 * @date 2025/12/3 23:49
 */
public class VendingMachineBlock extends BaseBlock {

    public VendingMachineBlock(Properties p_49795_) {
        super(p_49795_);
    }

    protected VoxelShape getShape(BlockState p_48760_, BlockGetter p_48761_, BlockPos p_48762_, CollisionContext p_48763_) {
        return makeShape();
    }

    public VoxelShape makeShape(){
        return Shapes.box(0, 0, 0, 1, 2, 1);
    }
}

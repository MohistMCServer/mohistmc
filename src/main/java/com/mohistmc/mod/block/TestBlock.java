package com.mohistmc.mod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * @author Mgazul
 * @date 2025/12/3 01:08
 */
public class TestBlock extends Block {

    public TestBlock(Properties p_49795_) {
        super(p_49795_);
    }

    protected VoxelShape getShape(BlockState p_48760_, BlockGetter p_48761_, BlockPos p_48762_, CollisionContext p_48763_) {
        return makeShape();
    }

    public VoxelShape makeShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.9375, 0.9375, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 0.9375, 0.0625, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.9375, 0, 1, 1, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0, 0.0625, 0.9375, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0.9375, 0.0625, 0.9375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.0625, 0.9375, 1, 0.9375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.0625, 0.0625, 0.0625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.9375, 0.0625, 0.0625, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.9375, 1, 0.0625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0, 0, 1, 0.0625, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.9375, 0, 0.9375, 1, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.0625, 0, 1, 0.9375, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.0625, 0.0625, 0.9375, 0.9375, 0.9375), BooleanOp.OR);

        return shape;
    }
}

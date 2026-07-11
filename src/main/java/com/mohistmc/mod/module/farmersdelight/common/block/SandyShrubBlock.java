package com.mohistmc.mod.module.farmersdelight.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.IShearable;

public class SandyShrubBlock extends BushBlock implements IShearable, BonemealableBlock
{
	public static final MapCodec<SandyShrubBlock> CODEC = simpleCodec(SandyShrubBlock::new);

	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

	public SandyShrubBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
		return state.is(BlockTags.SAND);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
		return true;
	}
}

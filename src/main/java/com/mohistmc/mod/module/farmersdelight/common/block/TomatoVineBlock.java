package com.mohistmc.mod.module.farmersdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import com.mohistmc.mod.module.farmersdelight.common.Configuration;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModBlocks;
import com.mohistmc.mod.module.farmersdelight.common.tag.ModTags;

// Renamed to TomatoBlock. This class will be removed in the next minor/major release.
@Deprecated(forRemoval = true)
public class TomatoVineBlock extends TomatoBlock
{
	public TomatoVineBlock(Properties properties) {
		super(properties);
	}

	protected TomatoVineBlock(Properties properties, boolean dummy) {
		super(properties, dummy);
	}

	@Deprecated(forRemoval = true)
	public void attemptRopeClimb(ServerLevel level, BlockPos pos, RandomSource random) {
		if (random.nextFloat() < 0.3F) {
			BlockPos posAbove = pos.above();
			BlockState stateAbove = level.getBlockState(posAbove);
			boolean canClimb = Configuration.ENABLE_TOMATO_VINE_CLIMBING_TAGGED_ROPES.get() ? stateAbove.is(ModTags.Blocks.ROPES) : stateAbove.is(ModBlocks.ROPE.get());
			if (canClimb) {
				int vineHeight;
				for (vineHeight = 1; level.getBlockState(pos.below(vineHeight)).is(this); ++vineHeight) {
				}
				if (vineHeight < 3) {
					level.setBlockAndUpdate(posAbove, defaultBlockState().setValue(ROPELOGGED, true));
				}
			}
		}
	}
}

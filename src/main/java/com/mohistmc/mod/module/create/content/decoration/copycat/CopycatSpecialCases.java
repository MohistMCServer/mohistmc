package com.mohistmc.mod.module.create.content.decoration.copycat;

import com.mohistmc.mod.module.create.content.decoration.palettes.GlassPaneBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CopycatSpecialCases {

    public static boolean isBarsMaterial(BlockState material) {
        Block block = material.getBlock();
        return block instanceof IronBarsBlock && !(block instanceof GlassPaneBlock) && !(block instanceof StainedGlassPaneBlock) && block != Blocks.GLASS_PANE;
    }

    public static boolean isTrapdoorMaterial(BlockState material) {
        return material.getBlock() instanceof TrapDoorBlock && material.hasProperty(TrapDoorBlock.HALF) && material.hasProperty(
            TrapDoorBlock.OPEN) && material.hasProperty(TrapDoorBlock.FACING);
    }

}

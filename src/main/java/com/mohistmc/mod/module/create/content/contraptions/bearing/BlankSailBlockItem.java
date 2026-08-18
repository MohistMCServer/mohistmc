package com.mohistmc.mod.module.create.content.contraptions.bearing;

import com.zurrtum.create.AllBlocks;
import java.util.Map;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BlankSailBlockItem extends BlockItem {
    public BlankSailBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Block getBlock() {
        return super.getBlock();
    }

    @Override
    public void registerBlocks(Map<Block, Item> map, Item item) {
        AllBlocks.SAIL.forEach(block -> map.put(block, item));
    }
}

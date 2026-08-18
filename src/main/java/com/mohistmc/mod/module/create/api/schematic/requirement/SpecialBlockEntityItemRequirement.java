package com.mohistmc.mod.module.create.api.schematic.requirement;

import com.mohistmc.mod.module.create.content.schematics.requirement.ItemRequirement;
import net.minecraft.world.level.block.state.BlockState;

public interface SpecialBlockEntityItemRequirement {
    ItemRequirement getRequiredItems(BlockState state);
}

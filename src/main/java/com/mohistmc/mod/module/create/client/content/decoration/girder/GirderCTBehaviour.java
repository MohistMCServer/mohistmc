package com.mohistmc.mod.module.create.client.content.decoration.girder;

import com.mohistmc.mod.module.create.client.AllSpriteShifts;
import com.mohistmc.mod.module.create.client.foundation.block.connected.CTSpriteShiftEntry;
import com.mohistmc.mod.module.create.client.foundation.block.connected.ConnectedTextureBehaviour;
import com.mohistmc.mod.module.create.content.decoration.girder.GirderBlock;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class GirderCTBehaviour extends ConnectedTextureBehaviour.Base {

    @Override
    @Nullable
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        if (!state.hasProperty(GirderBlock.X)) {
            return null;
        }
        return !state.getValue(GirderBlock.X) && !state.getValue(GirderBlock.Z) && direction.getAxis() != Axis.Y ?
            AllSpriteShifts.GIRDER_POLE : null;
    }

    @Override
    public boolean connectsTo(
        BlockState state,
        BlockState other,
        BlockAndTintGetter reader,
        BlockPos pos,
        BlockPos otherPos,
        Direction face
    ) {
        if (other.getBlock() != state.getBlock()) {
            return false;
        }
        return !other.getValue(GirderBlock.X) && !other.getValue(GirderBlock.Z);
    }

}

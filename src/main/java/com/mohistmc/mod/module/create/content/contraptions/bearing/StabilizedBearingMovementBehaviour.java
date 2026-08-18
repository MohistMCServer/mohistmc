package com.mohistmc.mod.module.create.content.contraptions.bearing;

import com.mohistmc.mod.module.create.api.behaviour.movement.MovementBehaviour;
import com.mohistmc.mod.module.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class StabilizedBearingMovementBehaviour extends MovementBehaviour {
    @Override
    @Nullable
    public ItemStack canBeDisabledVia(MovementContext context) {
        return null;
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }
}

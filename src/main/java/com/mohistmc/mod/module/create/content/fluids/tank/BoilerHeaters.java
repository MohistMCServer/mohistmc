package com.mohistmc.mod.module.create.content.fluids.tank;

import com.mohistmc.mod.module.create.AllBlockTags;
import com.mohistmc.mod.module.create.AllBlocks;
import com.mohistmc.mod.module.create.api.boiler.BoilerHeater;
import com.mohistmc.mod.module.create.api.registry.SimpleRegistry;
import com.mohistmc.mod.module.create.content.processing.burner.BlazeBurnerBlock;
import com.mohistmc.mod.module.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.mohistmc.mod.module.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BoilerHeaters {
    public static void register() {
        BoilerHeater.REGISTRY.register(AllBlocks.BLAZE_BURNER, BoilerHeater.BLAZE_BURNER);
        BoilerHeater.REGISTRY.registerProvider(SimpleRegistry.Provider.forBlockTag(
            AllBlockTags.PASSIVE_BOILER_HEATERS,
            BoilerHeater.PASSIVE
        ));
    }

    public static int passive(Level level, BlockPos pos, BlockState state) {
        return BlockHelper.isNotUnheated(state) ? BoilerHeater.PASSIVE_HEAT : BoilerHeater.NO_HEAT;
    }

    public static int blazeBurner(Level level, BlockPos pos, BlockState state) {
        HeatLevel value = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
        if (value == HeatLevel.NONE) {
            return BoilerHeater.NO_HEAT;
        }
        if (value == HeatLevel.SEETHING) {
            return 2;
        }
        if (value.isAtLeast(HeatLevel.FADING)) {
            return 1;
        }
        return BoilerHeater.PASSIVE_HEAT;
    }
}

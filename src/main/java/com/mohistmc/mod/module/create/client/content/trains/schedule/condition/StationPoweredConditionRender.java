package com.mohistmc.mod.module.create.client.content.trains.schedule.condition;

import com.mohistmc.mod.module.create.catnip.data.Pair;
import com.mohistmc.mod.module.create.client.content.trains.schedule.IScheduleInput;
import com.mohistmc.mod.module.create.client.foundation.utility.CreateLang;
import com.mohistmc.mod.module.create.content.trains.schedule.condition.StationPoweredCondition;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class StationPoweredConditionRender implements IScheduleInput<StationPoweredCondition> {
    @Override
    public Pair<ItemStack, Component> getSummary(StationPoweredCondition input) {
        return Pair.of(ItemStack.EMPTY, CreateLang.translateDirect("schedule.condition.powered"));
    }
}

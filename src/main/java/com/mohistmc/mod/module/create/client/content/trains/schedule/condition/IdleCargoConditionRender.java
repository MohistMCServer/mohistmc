package com.mohistmc.mod.module.create.client.content.trains.schedule.condition;

import com.mohistmc.mod.module.create.catnip.data.Pair;
import com.mohistmc.mod.module.create.client.foundation.utility.CreateLang;
import com.mohistmc.mod.module.create.content.trains.schedule.condition.IdleCargoCondition;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class IdleCargoConditionRender extends TimedWaitConditionRender<IdleCargoCondition> {
    @Override
    public Pair<ItemStack, Component> getSummary(IdleCargoCondition input) {
        return Pair.of(
            ItemStack.EMPTY,
            CreateLang.translateDirect("schedule.condition.idle_short", formatTime(input, true))
        );
    }
}

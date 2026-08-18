package com.mohistmc.mod.module.create.client.content.trains.schedule.destination;

import com.google.common.collect.ImmutableList;
import com.mohistmc.mod.module.create.AllItems;
import com.mohistmc.mod.module.create.catnip.data.Pair;
import com.mohistmc.mod.module.create.client.foundation.gui.widget.FilterEditBox;
import com.mohistmc.mod.module.create.client.foundation.utility.CreateLang;
import com.mohistmc.mod.module.create.content.trains.schedule.destination.DestinationInstruction;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

public class DestinationInstructionRender extends TextScheduleInstructionRender<DestinationInstruction> {
    @Override
    public Pair<ItemStack, Component> getSummary(DestinationInstruction input) {
        return Pair.of(AllItems.TRACK_STATION.getDefaultInstance(), Component.literal(input.getLabelText()));
    }

    @Override
    public ItemStack getSecondLineIcon() {
        return AllItems.TRACK_STATION.getDefaultInstance();
    }

    @Override
    public List<Component> getSecondLineTooltip(int slot) {
        return ImmutableList.of(
            CreateLang.translateDirect("schedule.instruction.filter_edit_box"),
            CreateLang.translateDirect("schedule.instruction.filter_edit_box_1").withStyle(ChatFormatting.GRAY),
            CreateLang.translateDirect("schedule.instruction.filter_edit_box_2").withStyle(ChatFormatting.DARK_GRAY),
            CreateLang.translateDirect("schedule.instruction.filter_edit_box_3").withStyle(ChatFormatting.DARK_GRAY)
        );
    }

    @Override
    protected void modifyEditBox(FilterEditBox box) {
        box.setFilter(s -> StringUtils.countMatches(s, '*') <= 3);
    }
}
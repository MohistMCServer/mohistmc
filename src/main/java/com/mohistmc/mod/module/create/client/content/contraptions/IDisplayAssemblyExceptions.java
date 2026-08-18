package com.mohistmc.mod.module.create.client.content.contraptions;

import com.mohistmc.mod.module.create.client.catnip.lang.FontHelper.Palette;
import com.mohistmc.mod.module.create.client.foundation.item.TooltipHelper;
import com.mohistmc.mod.module.create.client.foundation.utility.CreateLang;
import com.mohistmc.mod.module.create.content.contraptions.AssemblyException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public interface IDisplayAssemblyExceptions {

    default boolean addExceptionToTooltip(List<Component> tooltip) {
        AssemblyException e = getLastAssemblyException();
        if (e == null) {
            return false;
        }

        if (!tooltip.isEmpty()) {
            tooltip.add(CommonComponents.EMPTY);
        }

        CreateLang.translate("gui.assembly.exception").style(ChatFormatting.GOLD).forGoggles(tooltip);

        String text = e.component.getString();
        Arrays.stream(text.split("\n")).forEach(l -> TooltipHelper.cutStringTextComponent(l, Palette.GRAY_AND_WHITE)
            .forEach(c -> CreateLang.builder().add(c).forGoggles(tooltip)));

        return true;
    }

    @Nullable AssemblyException getLastAssemblyException();

}

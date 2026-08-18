package com.mohistmc.mod.module.create.client.content.redstone.displayLink.source;

import com.mohistmc.mod.module.create.api.behaviour.display.DisplaySource;
import com.mohistmc.mod.module.create.client.foundation.gui.ModularGuiLineBuilder;
import com.mohistmc.mod.module.create.client.foundation.utility.CreateLang;
import com.mohistmc.mod.module.create.content.redstone.displayLink.DisplayLinkContext;

public class RedstonePowerDisplaySourceRender extends SingleLineDisplaySourceRender {
    @Override
    public void initConfigurationWidgets(
        DisplaySource source,
        DisplayLinkContext context,
        ModularGuiLineBuilder builder,
        boolean isFirstLine
    ) {
        super.initConfigurationWidgets(source, context, builder, isFirstLine);
        if (isFirstLine) {
            return;
        }
        builder.addSelectionScrollInput(
            0,
            120,
            (si, l) -> si.forOptions(CreateLang.translatedOptions(
                "display_source.redstone_power",
                "number",
                "progress_bar"
            )).titled(CreateLang.translateDirect("display_source.redstone_power.display")),
            "Mode"
        );
    }
}

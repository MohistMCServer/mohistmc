package com.mohistmc.mod.module.create.client.content.redstone.displayLink.source;

import com.mohistmc.mod.module.create.api.behaviour.display.DisplaySource;
import com.mohistmc.mod.module.create.client.foundation.gui.ModularGuiLineBuilder;
import com.mohistmc.mod.module.create.client.foundation.utility.CreateLang;
import com.mohistmc.mod.module.create.content.redstone.displayLink.DisplayLinkContext;

public class KineticStressDisplaySourceRender extends SingleLineDisplaySourceRender {
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
                "display_source.kinetic_stress",
                "progress_bar",
                "percent",
                "current",
                "max",
                "remaining"
            )).titled(CreateLang.translateDirect("display_source.kinetic_stress.display")),
            "Mode"
        );
    }
}

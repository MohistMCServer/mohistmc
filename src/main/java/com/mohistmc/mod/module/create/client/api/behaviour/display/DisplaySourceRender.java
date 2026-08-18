package com.mohistmc.mod.module.create.client.api.behaviour.display;

import com.mohistmc.mod.module.create.api.behaviour.display.DisplaySource;
import com.mohistmc.mod.module.create.client.foundation.gui.ModularGuiLineBuilder;
import com.mohistmc.mod.module.create.content.redstone.displayLink.DisplayLinkContext;

public interface DisplaySourceRender {
    void initConfigurationWidgets(
        DisplaySource source,
        DisplayLinkContext context,
        ModularGuiLineBuilder builder,
        boolean isFirstLine
    );
}

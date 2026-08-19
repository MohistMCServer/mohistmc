package com.mohistmc.mod.module.ponder.api.element;

import com.mohistmc.mod.module.ponder.foundation.PonderScene;
import com.mohistmc.mod.module.ponder.foundation.ui.PonderUI;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface PonderOverlayElement extends PonderElement {

    void render(PonderScene scene, PonderUI screen, GuiGraphicsExtractor graphics, float partialTicks);

}

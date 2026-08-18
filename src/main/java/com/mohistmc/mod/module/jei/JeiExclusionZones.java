package com.mohistmc.mod.module.jei;

import com.mohistmc.mod.module.create.client.foundation.gui.menu.AbstractSimiContainerScreen;
import java.util.List;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;

public class JeiExclusionZones implements IGuiContainerHandler<AbstractSimiContainerScreen<?>> {
    @Override
    public List<Rect2i> getGuiExtraAreas(AbstractSimiContainerScreen<?> containerScreen) {
        return containerScreen.getExtraAreas();
    }
}

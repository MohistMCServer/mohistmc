package com.mohistmc.mod.module.create.client.foundation.gui.widget;

import com.zurrtum.create.client.catnip.gui.widget.AbstractSimiWidget;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class TooltipArea extends AbstractSimiWidget {

    public TooltipArea(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
        }
    }

    public TooltipArea withTooltip(List<Component> tooltip) {
        toolTip = tooltip;
        return this;
    }

}

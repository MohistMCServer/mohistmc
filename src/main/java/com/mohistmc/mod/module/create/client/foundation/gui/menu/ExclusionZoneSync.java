package com.mohistmc.mod.module.create.client.foundation.gui.menu;

import java.util.List;
import net.minecraft.client.renderer.Rect2i;

public interface ExclusionZoneSync {
    void set(List<Rect2i> extraAreas);

    void clear();
}

package com.mohistmc.mod.module.ponder.foundation.instruction;

import com.mohistmc.mod.module.ponder.api.PonderPalette;
import com.mohistmc.mod.module.ponder.api.scene.Selection;
import com.mohistmc.mod.module.ponder.foundation.PonderScene;

public class OutlineSelectionInstruction extends TickingInstruction {

    private final PonderPalette color;
    private final Object slot;
    private final Selection selection;

    public OutlineSelectionInstruction(PonderPalette color, Object slot, Selection selection, int ticks) {
        super(false, ticks);
        this.color = color;
        this.slot = slot;
        this.selection = selection;
    }

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        selection.makeOutline(scene.getOutliner(), slot).lineWidth(1 / 16.0f).colored(color.getColor());
    }

}
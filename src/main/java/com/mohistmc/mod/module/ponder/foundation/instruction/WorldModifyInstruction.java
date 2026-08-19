package com.mohistmc.mod.module.ponder.foundation.instruction;

import com.mohistmc.mod.module.ponder.api.element.WorldSectionElement;
import com.mohistmc.mod.module.ponder.api.scene.Selection;
import com.mohistmc.mod.module.ponder.foundation.PonderScene;

public abstract class WorldModifyInstruction extends PonderInstruction {

    private final Selection selection;

    public WorldModifyInstruction(Selection selection) {
        this.selection = selection;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public void tick(PonderScene scene) {
        runModification(selection, scene);
        if (needsRedraw()) {
            scene.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
        }
    }

    protected abstract void runModification(Selection selection, PonderScene scene);

    protected abstract boolean needsRedraw();

}
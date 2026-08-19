package com.mohistmc.mod.module.ponder.api.scene;

import com.mohistmc.mod.module.ponder.foundation.PonderScene;
import com.mohistmc.mod.module.ponder.foundation.instruction.PonderInstruction;

import java.util.function.Consumer;

public interface DebugInstructions {
    void debugSchematic();

    void addInstructionInstance(PonderInstruction instruction);

    void enqueueCallback(Consumer<PonderScene> callback);
}

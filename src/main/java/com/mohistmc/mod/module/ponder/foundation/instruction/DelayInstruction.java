package com.mohistmc.mod.module.ponder.foundation.instruction;

public class DelayInstruction extends TickingInstruction {

    public DelayInstruction(int ticks) {
        super(true, ticks);
    }

}

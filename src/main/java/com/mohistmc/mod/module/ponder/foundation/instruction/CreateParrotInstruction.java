package com.mohistmc.mod.module.ponder.foundation.instruction;

import com.mohistmc.mod.module.ponder.api.element.ParrotElement;
import net.minecraft.core.Direction;

public class CreateParrotInstruction extends FadeIntoSceneInstruction<ParrotElement> {

    public CreateParrotInstruction(int fadeInTicks, Direction fadeInFrom, ParrotElement element) {
        super(fadeInTicks, fadeInFrom, element);
    }

    @Override
    protected Class<ParrotElement> getElementClass() {
        return ParrotElement.class;
    }

}